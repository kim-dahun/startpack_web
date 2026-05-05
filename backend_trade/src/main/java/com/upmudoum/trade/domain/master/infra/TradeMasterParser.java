package com.upmudoum.trade.domain.master.infra;

import com.upmudoum.trade.domain.master.dto.TradeMasterImportRowDto;
import com.upmudoum.trade.domain.master.service.TradeMasterSourceRegistry;
import com.upmudoum.trade.domain.master.vo.TradeMasterParserStrategy;
import com.upmudoum.trade.domain.master.vo.TradeMasterSourceDefinition;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TradeMasterParser {

    private final TradeMasterSourceRegistry sourceRegistry;

    public TradeMasterParser(TradeMasterSourceRegistry sourceRegistry) {
        this.sourceRegistry = sourceRegistry;
    }

    public List<TradeMasterImportRowDto> parse(TradeMasterType masterType, byte[] content) {
        TradeMasterSourceDefinition definition = sourceRegistry.get(masterType);
        if (!definition.isParserImplementedYn()) {
            throw new IllegalArgumentException("parser is not implemented for " + masterType);
        }
        Charset charset = Charset.forName(definition.getCharset());
        return switch (definition.getParserStrategy()) {
            case DOMESTIC_STOCK_FIXED_WIDTH -> parseDomesticStock(definition, content, charset);
            case DOMESTIC_ELW_FIXED_WIDTH -> parseFixedWidth(definition, content, charset, 0, 9, 21, 50, "KR");
            case DOMESTIC_BOND_FIXED_WIDTH -> parseFixedWidth(definition, content, charset, 4, 16, 16, -26, "KR");
            case PIPE_DELIMITED_MASTER -> parseDelimited(definition, content, charset, "\\|", 1, 3, "KR");
            case TAB_DELIMITED_MASTER, OVERSEAS_STOCK_TAB -> parseDelimited(definition, content, charset, "\t", 4, 6, "US");
            case SECTOR_FIXED_WIDTH -> parseFixedWidth(definition, content, charset, 1, 5, 3, 43, "KR");
            case THEME_FIXED_WIDTH -> parseTheme(definition, content, charset);
            case NOT_IMPLEMENTED -> throw new IllegalArgumentException("parser is not implemented for " + masterType);
        };
    }

    private List<TradeMasterImportRowDto> parseDomesticStock(TradeMasterSourceDefinition definition, byte[] content, Charset charset) {
        List<TradeMasterImportRowDto> rows = new ArrayList<>();
        for (byte[] line : lines(content)) {
            if (line.length < 61) {
                continue;
            }
            String rawCode = text(line, 0, Math.min(9, line.length), charset);
            String itemCode = rawCode.length() > 6 ? rawCode.substring(rawCode.length() - 6) : rawCode;
            String itemName = text(line, 21, Math.min(61, line.length), charset);
            if (itemCode.isBlank() || itemName.isBlank()) {
                continue;
            }
            TradeMasterImportRowDto row = new TradeMasterImportRowDto();
            row.setItemCode(itemCode);
            row.setItemName(itemName);
            row.setMarketCode(definition.getMasterType().name());
            row.setCountryCode("KR");
            row.setSectorName("");
            applyDomesticMeta(row, line, charset);
            row.setRaw(Map.of(
                    "masterType", definition.getMasterType().name(),
                    "parserStrategy", definition.getParserStrategy().name(),
                    "rawPrefixHex", HexFormat.of().formatHex(line, 0, Math.min(line.length, 64))
            ));
            rows.add(row);
        }
        return rows;
    }

    private List<TradeMasterImportRowDto> parseFixedWidth(
            TradeMasterSourceDefinition definition,
            byte[] content,
            Charset charset,
            int codeStart,
            int codeEnd,
            int nameStart,
            int nameEnd,
            String countryCode
    ) {
        List<TradeMasterImportRowDto> rows = new ArrayList<>();
        for (byte[] line : lines(content)) {
            int resolvedNameEnd = nameEnd < 0 ? Math.max(nameStart, line.length + nameEnd) : Math.min(nameEnd, line.length);
            String itemCode = text(line, codeStart, Math.min(codeEnd, line.length), charset);
            String itemName = text(line, nameStart, resolvedNameEnd, charset);
            addRow(rows, definition, itemCode, itemName, countryCode, line);
        }
        return rows;
    }

    private List<TradeMasterImportRowDto> parseDelimited(
            TradeMasterSourceDefinition definition,
            byte[] content,
            Charset charset,
            String delimiterRegex,
            int codeIndex,
            int nameIndex,
            String defaultCountryCode
    ) {
        List<TradeMasterImportRowDto> rows = new ArrayList<>();
        String text = new String(content, charset);
        for (String line : text.split("\\R")) {
            if (line.isBlank()) {
                continue;
            }
            String[] columns = line.split(delimiterRegex, -1);
            String itemCode = column(columns, codeIndex);
            String itemName = column(columns, nameIndex);
            String countryCode = column(columns, 0).isBlank() ? defaultCountryCode : column(columns, 0);
            addRow(rows, definition, itemCode, itemName, countryCode, line.getBytes(charset));
        }
        return rows;
    }

    private List<TradeMasterImportRowDto> parseTheme(TradeMasterSourceDefinition definition, byte[] content, Charset charset) {
        List<TradeMasterImportRowDto> rows = new ArrayList<>();
        for (byte[] line : lines(content)) {
            String themeCode = text(line, 0, Math.min(3, line.length), charset);
            String itemCode = text(line, Math.max(0, line.length - 10), line.length, charset);
            String itemName = text(line, 3, Math.max(3, line.length - 10), charset);
            addRow(rows, definition, itemCode.isBlank() ? themeCode : itemCode, itemName, "KR", line);
        }
        return rows;
    }

    private void addRow(
            List<TradeMasterImportRowDto> rows,
            TradeMasterSourceDefinition definition,
            String itemCode,
            String itemName,
            String countryCode,
            byte[] rawLine
    ) {
        if (itemCode == null || itemCode.isBlank() || itemName == null || itemName.isBlank()) {
            return;
        }
        TradeMasterImportRowDto row = new TradeMasterImportRowDto();
        row.setItemCode(itemCode.trim());
        row.setItemName(itemName.trim());
        row.setMarketCode(definition.getMasterType().name());
        row.setCountryCode(countryCode == null || countryCode.isBlank() ? "KR" : countryCode.trim());
        row.setSectorName("");
        row.setRaw(Map.of(
                "masterType", definition.getMasterType().name(),
                "parserStrategy", definition.getParserStrategy().name(),
                "rawPrefixHex", HexFormat.of().formatHex(rawLine, 0, Math.min(rawLine.length, 64))
        ));
        rows.add(row);
    }

    private void applyDomesticMeta(TradeMasterImportRowDto row, byte[] line, Charset charset) {
        if (line.length < 228) {
            return;
        }
        int start = line.length - 228;
        row.setMarketCap(decimal(line, start + 59, start + 68, charset));
        row.setSalesAmount(decimal(line, start + 177, start + 186, charset));
        row.setOperatingProfit(decimal(line, start + 186, start + 195, charset));
    }

    private List<byte[]> lines(byte[] content) {
        List<byte[]> rows = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < content.length; i++) {
            if (content[i] == '\n') {
                int end = i > start && content[i - 1] == '\r' ? i - 1 : i;
                rows.add(java.util.Arrays.copyOfRange(content, start, end));
                start = i + 1;
            }
        }
        if (start < content.length) {
            rows.add(java.util.Arrays.copyOfRange(content, start, content.length));
        }
        return rows;
    }

    private String text(byte[] line, int start, int end, Charset charset) {
        if (start >= line.length || end <= start) {
            return "";
        }
        return new String(line, start, end - start, charset).trim();
    }

    private BigDecimal decimal(byte[] line, int start, int end, Charset charset) {
        String value = text(line, start, Math.min(end, line.length), charset).replace(",", "");
        if (value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String column(String[] columns, int index) {
        if (index < 0 || index >= columns.length) {
            return "";
        }
        return columns[index].trim();
    }
}

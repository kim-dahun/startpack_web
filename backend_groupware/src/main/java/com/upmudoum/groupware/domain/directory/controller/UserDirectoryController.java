package com.upmudoum.groupware.domain.directory.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upmudoum.groupware.common.RequestContextResolver;
import com.upmudoum.groupware.domain.directory.dto.UserDirectoryItem;
import com.upmudoum.groupware.domain.directory.service.UserDirectoryService;

@RestController
@RequestMapping("/api/groupware/directory")
public class UserDirectoryController {

    private final UserDirectoryService userDirectoryService;

    public UserDirectoryController(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    @GetMapping("/users")
    public List<UserDirectoryItem> searchUsers(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestParam(required = false) String keyword) {
        return userDirectoryService.search(comCd, keyword);
    }
}

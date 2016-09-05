package com.epam.server.service;

import com.epam.common.dto.ServerInfoDto;
import com.epam.common.service.ServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * Created by Ahmed_Khan on 4/22/2016.
 */
@Service
public class ServerInfoServiceImpl implements ServerInfoService {

    private InetAddress inetAddress;
    private Clock clock;

    @Autowired
    public ServerInfoServiceImpl(InetAddress inetAddress, Clock clock) {
        this.inetAddress = inetAddress;
        this.clock = clock;
    }

    public ServerInfoDto getServerInfo() {
        return new ServerInfoDto(inetAddress.getHostAddress(), ZonedDateTime.now(clock));
    }
}

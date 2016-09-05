package com.epam.server.service;

import com.epam.common.dto.ServerInfoDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.InetAddress;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerInfoServiceImplTest {
    private ServerInfoServiceImpl serverInfoService;
    @Mock
    private InetAddress inetAddress;
    private Clock clock;

    @Before
    public void init() {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        serverInfoService = new ServerInfoServiceImpl(inetAddress, clock);
    }

    @Test
    public void checkServerInfo() {
        String serverIp = "192.168.0.1";
        ServerInfoDto expectedServerInfoDto = new ServerInfoDto(serverIp, ZonedDateTime.now(clock));
        when(inetAddress.getHostAddress()).thenReturn(serverIp);
        Assert.assertEquals(expectedServerInfoDto, serverInfoService.getServerInfo());
        verify(inetAddress, times(1)).getHostAddress();
    }
}

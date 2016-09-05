package com.epam.common.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Created by Ahmed_Khan on 4/22/2016.
 */
final public class ServerInfoDto implements Serializable {

    private String ip;
    private ZonedDateTime zonedDateTime;

    public ServerInfoDto(String ip, ZonedDateTime zonedDateTime) {
        this.ip = ip;
        this.zonedDateTime = zonedDateTime;
    }

    public String getIp() {
        return ip;
    }

    public ZonedDateTime getDate() {
        return zonedDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerInfoDto that = (ServerInfoDto) o;
        return Objects.equals(ip, that.ip) &&
                Objects.equals(zonedDateTime, that.zonedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, zonedDateTime);
    }

    @Override
    public String toString() {
        return "ServerInfoDto{" +
                "ip='" + ip + '\'' +
                ", zonedDateTime=" + zonedDateTime +
                '}';
    }
}

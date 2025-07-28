package com.resualbeartefact.data;

import lombok.Data;

@Data
public class SqlInfo {
        private String query;
        private String host;
        private Integer port;
        private String db;
        private String user;
        private String password;
}


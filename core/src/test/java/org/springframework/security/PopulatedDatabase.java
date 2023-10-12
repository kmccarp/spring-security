/*
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Singleton which provides a populated database connection for all JDBC-related unit
 * tests.
 *
 * @author Ben Alex
 */
public final class PopulatedDatabase {

	private static TestDataSource dataSource;

	private PopulatedDatabase() {
	}

	public static DataSource getDataSource() {
		if (dataSource == null) {
			setupDataSource();
		}
		return dataSource;
	}

	private static void setupDataSource() {
		dataSource = new TestDataSource("springsecuritytest");
		JdbcTemplate template = new JdbcTemplate(dataSource);
		template.execute(
				"CREATE TABLE USERS(USERNAME VARCHAR_IGNORECASE(50) NOT NULL PRIMARY KEY,PASSWORD VARCHAR_IGNORECASE(500) NOT NULL,ENABLED BOOLEAN NOT NULL)");
		template.execute(
				"CREATE TABLE AUTHORITIES(USERNAME VARCHAR_IGNORECASE(50) NOT NULL,AUTHORITY VARCHAR_IGNORECASE(50) NOT NULL,CONSTRAINT FK_AUTHORITIES_USERS FOREIGN KEY(USERNAME) REFERENCES USERS(USERNAME))");
		template.execute("CREATE UNIQUE INDEX IX_AUTH_USERNAME ON AUTHORITIES(USERNAME,AUTHORITY)");
		template.execute(
				"CREATE TABLE ACL_OBJECT_IDENTITY(ID BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0)  NOT NULL PRIMARY KEY,OBJECT_IDENTITY VARCHAR_IGNORECASE(250) NOT NULL,PARENT_OBJECT BIGINT,ACL_CLASS VARCHAR_IGNORECASE(250) NOT NULL,CONSTRAINT UNIQUE_OBJECT_IDENTITY UNIQUE(OBJECT_IDENTITY),CONSTRAINT SYS_FK_3 FOREIGN KEY(PARENT_OBJECT) REFERENCES ACL_OBJECT_IDENTITY(ID))");
		template.execute(
				"CREATE TABLE ACL_PERMISSION(ID BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0)  NOT NULL PRIMARY KEY,ACL_OBJECT_IDENTITY BIGINT NOT NULL,RECIPIENT VARCHAR_IGNORECASE(100) NOT NULL,MASK INTEGER NOT NULL,CONSTRAINT UNIQUE_RECIPIENT UNIQUE(ACL_OBJECT_IDENTITY,RECIPIENT),CONSTRAINT SYS_FK_7 FOREIGN KEY(ACL_OBJECT_IDENTITY) REFERENCES ACL_OBJECT_IDENTITY(ID))");
		template.execute("SET IGNORECASE TRUE");
		template.execute("INSERT INTO USERS VALUES('dianne','emu',TRUE)");
		template.execute("INSERT INTO USERS VALUES('rod','koala',TRUE)");
		template.execute("INSERT INTO USERS VALUES('peter','opal',FALSE)");
		template.execute("INSERT INTO USERS VALUES('scott','wombat',TRUE)");
		template.execute("INSERT INTO USERS VALUES('cooper','kookaburra',TRUE)");
		template.execute("INSERT INTO AUTHORITIES VALUES('rod','ROLE_TELLER')");
		template.execute("INSERT INTO AUTHORITIES VALUES('rod','ROLE_SUPERVISOR')");
		template.execute("INSERT INTO AUTHORITIES VALUES('dianne','ROLE_TELLER')");
		template.execute("INSERT INTO AUTHORITIES VALUES('scott','ROLE_TELLER')");
		template.execute("INSERT INTO AUTHORITIES VALUES('peter','ROLE_TELLER')");
		template.execute(
				"INSERT INTO acl_object_identity VALUES (1, 'org.springframework.security.acl.DomainObject:1', null, 'org.springframework.security.acl.basic.SimpleAclEntry');");
		template.execute(
				"INSERT INTO acl_object_identity VALUES (2, 'org.springframework.security.acl.DomainObject:2', 1, 'org.springframework.security.acl.basic.SimpleAclEntry');");
		template.execute(
				"INSERT INTO acl_object_identity VALUES (3, 'org.springframework.security.acl.DomainObject:3', 1, 'org.springframework.security.acl.basic.SimpleAclEntry');");
		template.execute(
				"INSERT INTO acl_object_identity VALUES (4, 'org.springframework.security.acl.DomainObject:4', 1, 'org.springframework.security.acl.basic.SimpleAclEntry');");
		template.execute(
				"INSERT INTO acl_object_identity VALUES (5, 'org.springframework.security.acl.DomainObject:5', 3, 'org.springframework.security.acl.basic.SimpleAclEntry');");
		template.execute(
				"INSERT INTO acl_object_identity VALUES (6, 'org.springframework.security.acl.DomainObject:6', 3, 'org.springframework.security.acl.basic.SimpleAclEntry');");
		// ----- BEGIN deviation from normal sample data load script -----
		template.execute(
				"INSERT INTO acl_object_identity VALUES (7, 'org.springframework.security.acl.DomainObject:7', 3, 'some.invalid.acl.entry.class');");
		// ----- FINISH deviation from normal sample data load script -----
		template.execute("INSERT INTO acl_permission VALUES (null, 1, 'ROLE_SUPERVISOR', 1);");
		template.execute("INSERT INTO acl_permission VALUES (null, 2, 'ROLE_SUPERVISOR', 0);");
		template.execute("INSERT INTO acl_permission VALUES (null, 2, 'rod', 2);");
		template.execute("INSERT INTO acl_permission VALUES (null, 3, 'scott', 14);");
		template.execute("INSERT INTO acl_permission VALUES (null, 6, 'scott', 1);");
		createGroupTables(template);
		insertGroupData(template);
	}

	public static void createGroupTables(JdbcTemplate template) {
		// Group tables and data
		template.execute(
				"CREATE TABLE GROUPS(ID BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) PRIMARY KEY, GROUP_NAME VARCHAR_IGNORECASE(50) NOT NULL)");
		template.execute(
				"CREATE TABLE GROUP_AUTHORITIES(GROUP_ID BIGINT NOT NULL, AUTHORITY VARCHAR(50) NOT NULL, CONSTRAINT FK_GROUP_AUTHORITIES_GROUP FOREIGN KEY(GROUP_ID) REFERENCES GROUPS(ID))");
		template.execute(
				"CREATE TABLE GROUP_MEMBERS(ID BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) PRIMARY KEY, USERNAME VARCHAR(50) NOT NULL, GROUP_ID BIGINT NOT NULL, CONSTRAINT FK_GROUP_MEMBERS_GROUP FOREIGN KEY(GROUP_ID) REFERENCES GROUPS(ID))");
	}

	public static void insertGroupData(JdbcTemplate template) {
		template.execute("INSERT INTO USERS VALUES('jerry','password',TRUE)");
		template.execute("INSERT INTO USERS VALUES('tom','password',TRUE)");
		template.execute("INSERT INTO GROUPS VALUES (0, 'GROUP_0')");
		template.execute("INSERT INTO GROUPS VALUES (1, 'GROUP_1')");
		template.execute("INSERT INTO GROUPS VALUES (2, 'GROUP_2')");
		// Group 3 isn't used
		template.execute("INSERT INTO GROUPS VALUES (3, 'GROUP_3')");
		template.execute("INSERT INTO GROUP_AUTHORITIES VALUES (0, 'ROLE_A')");
		template.execute("INSERT INTO GROUP_AUTHORITIES VALUES (1, 'ROLE_B')");
		template.execute("INSERT INTO GROUP_AUTHORITIES VALUES (1, 'ROLE_C')");
		template.execute("INSERT INTO GROUP_AUTHORITIES VALUES (2, 'ROLE_A')");
		template.execute("INSERT INTO GROUP_AUTHORITIES VALUES (2, 'ROLE_B')");
		template.execute("INSERT INTO GROUP_AUTHORITIES VALUES (2, 'ROLE_C')");
		template.execute("INSERT INTO GROUP_AUTHORITIES VALUES (3, 'ROLE_D')");
		template.execute("INSERT INTO GROUP_AUTHORITIES VALUES (3, 'ROLE_E')");
		template.execute("INSERT INTO GROUP_MEMBERS VALUES (0, 'jerry', 0)");
		template.execute("INSERT INTO GROUP_MEMBERS VALUES (1, 'jerry', 1)");
		// tom has groups with overlapping roles
		template.execute("INSERT INTO GROUP_MEMBERS VALUES (2, 'tom', 1)");
		template.execute("INSERT INTO GROUP_MEMBERS VALUES (3, 'tom', 2)");
	}

}

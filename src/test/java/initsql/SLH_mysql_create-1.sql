/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2015/9/6 11:56:03                            */
/*==============================================================*/
drop table if exists user_depart;

drop table if exists user_jobs;

drop table if exists user_role;
drop table if exists tb_sysresource_item;

drop table if exists role_sysresource;

drop table if exists tb_clustersinglelogin;

drop table if exists tb_depart;

drop table if exists tb_jobs;
 
drop table if exists tb_operator;

drop table if exists tb_role;

drop table if exists tb_sysresource;


 
drop table if exists tb_user;



/*==============================================================*/
/* Table: role_sysresource                                      */
/*==============================================================*/
create table role_sysresource
(
   ID                   VARCHAR(32) comment 'ID',
   ROLEID               VARCHAR(32) comment 'ROLEID',
   SYSRESOURCEID        VARCHAR(32) comment 'SYSRESOURCEID'
);

alter table role_sysresource comment '角色资源表';

/*==============================================================*/
/* Table: tb_clustersinglelogin                                 */
/*==============================================================*/
create table tb_clustersinglelogin
(
   ID                   VARCHAR(32) not null comment 'ID',
   ACCOUNT              VARCHAR(500) comment '帐号',
   SESSIONID            VARCHAR(500) comment '会话ID',
   LASTDATE             VARCHAR(30) comment '最新时间',
   TRYCOUNT             INTEGER comment '登录次数',
   primary key (ID)
);

alter table tb_clustersinglelogin comment '集群单一登录支持';

/*==============================================================*/
/* Table: tb_depart                                             */
/*==============================================================*/
create table tb_depart
(
   ID                   VARCHAR(32) not null comment '部门ID',
   DEPTNO               VARCHAR(500) comment '部门编号',
   DEPTNAME             VARCHAR(500) comment '部门名称',
   LOCATION             VARCHAR(100) comment '部门地址',
   ISENABLED            INTEGER comment '0:开启 1:关闭',
   PARENTID             VARCHAR(32) comment '上级部门',
   SOURCES              INTEGER comment '0:本地 1:同步',
   NOTE                 VARCHAR(100) comment '备注',
   FLAG                 INTEGER default 1 comment '0:无效 1:有效',
   DEPT_REF_CODE        VARCHAR(3000) comment '部门关系编码',
   remark               VARCHAR(500) comment 'remark',
   primary key (ID)
);

alter table tb_depart comment '部门表';

/*==============================================================*/
/* Table: tb_jobs                                               */
/*==============================================================*/
create table tb_jobs
(
   ID                   VARCHAR(32) not null comment 'ID',
   NAME                 VARCHAR(500) comment 'NAME',
   primary key (ID)
);

alter table tb_jobs comment '职务表';

/*==============================================================*/
/* Table: tb_operator                                           */
/*==============================================================*/
create table tb_operator
(
   ID                   VARCHAR(32) not null comment 'ID',
   USER_NAME            VARCHAR(500) comment '用户名称',
   USER_ID              VARCHAR(32) comment '用户id',
   USER_OPERATOR        VARCHAR(200) comment '操作',
   OPERATORTIME         VARCHAR(30) comment '时间',
   USER_ACCOUNT         VARCHAR(500) comment '用户帐号',
   IP                   VARCHAR(100) comment 'IP',
   OPERATORSTATE        VARCHAR(10) comment '0 成功 1失败 2 非法访问',
   primary key (ID)
);

alter table tb_operator comment '操作日志';

/*==============================================================*/
/* Table: tb_role                                               */
/*==============================================================*/
create table tb_role
(
   ID                   VARCHAR(32) not null comment 'ID',
   ROLE_NAME            VARCHAR(100) comment 'ROLE_NAME',
   primary key (ID)
);

alter table tb_role comment '角色表';

/*==============================================================*/
/* Table: tb_sysresource                                        */
/*==============================================================*/
create table tb_sysresource
(
   ID                   VARCHAR(32) not null comment 'ID',
   CONTENT              VARCHAR(500) comment 'CONTENT',
   PARENT               VARCHAR(32) comment 'PARENT',
   MODULECAPTION        VARCHAR(200) comment 'MODULECAPTION',
   CAPTION              VARCHAR(200) comment 'CAPTION',
   CODE                 VARCHAR(200) not null comment 'CODE',
   SOURCE_TYPE          INTEGER default 0 comment '0:页面 1:按钮 2: 树菜单根 3树菜单节点',
   genFlag              VARCHAR(1000),
   orderNum             int,
   remark               VARCHAR(3000),
   primary key (ID)
);

alter table tb_sysresource comment '资源表';

/*==============================================================*/
/* Table: tb_sysresource_item                                   */
/*==============================================================*/
create table tb_sysresource_item
(
   ID                   VARCHAR(32) not null comment 'ID',
   SUPERIORRESOURCE     VARCHAR(32) comment 'SUPERIORRESOURCE',
   SUBORDINATERESOURCE  VARCHAR(32) comment 'SUBORDINATERESOURCE',
   primary key (ID)
);

alter table tb_sysresource_item comment '下级资源关系表';

/*==============================================================*/
/* Table: tb_user                                               */
/*==============================================================*/
CREATE TABLE tb_user (
  `ID` varchar(32) NOT NULL COMMENT '用户ID',
  `USER_NAME` varchar(200) NOT NULL COMMENT '用户名称',
  `ACCOUNT_NAME` varchar(200) NOT NULL COMMENT '登录账号',
  `PWD` varchar(32) NOT NULL COMMENT '密码',
  `IDCARD` varchar(50) DEFAULT NULL COMMENT '身份证号码',
  `PHONE` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `ADDRESS` varchar(500) DEFAULT NULL COMMENT '联系地址',
  `BEGINDATE` datetime DEFAULT NULL COMMENT '开始创建时间',
  `ENDDATE` datetime DEFAULT NULL COMMENT '结束时间',
  `ISOPEN` varchar(1) NOT NULL DEFAULT '0' COMMENT '是否有效   0：有效  1：无效',
  `USERSOURCE` varchar(1) NOT NULL COMMENT '0：同步    1：本地',
  `DATATYPE` varchar(2) NOT NULL COMMENT '0：平台后台用户    1 前端用户',
  `CREATOR` varchar(32) DEFAULT NULL COMMENT '创建者',
  `FLAG` int(11) DEFAULT '1' COMMENT '0:无效 1:有效 2 ：审核中',
  `PLAINTEXT_PWD` varchar(32) DEFAULT NULL COMMENT '匿名明文密码',
  `STATION` varchar(500) DEFAULT NULL COMMENT '岗位',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `org_id` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USER` (`USER_NAME`) USING HASH,
  UNIQUE KEY `ACCOUNT` (`ACCOUNT_NAME`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';
alter table tb_user comment '用户表';

/*==============================================================*/
/* Table: user_depart                                           */
/*==============================================================*/
create table user_depart
(
   ID                   VARCHAR(32) comment 'ID',
   USERID               VARCHAR(32) comment '用户id',
   DEPARTID             VARCHAR(32) comment '用户id'
);

alter table user_depart comment '用户部门表';

/*==============================================================*/
/* Table: user_jobs                                             */
/*==============================================================*/
create table user_jobs
(
   ID                   VARCHAR(32) comment 'ID',
   JOBID                VARCHAR(32) comment '职务id',
   USERID               VARCHAR(32) comment 'USERID'
);

alter table user_jobs comment '用户职务表';

/*==============================================================*/
/* Table: user_role                                             */
/*==============================================================*/
create table user_role
(
   ID                   VARCHAR(32) comment 'ID',
   USERID               VARCHAR(32) comment '用户ID',
   ROLEID               VARCHAR(32) comment '角色ID'
);

alter table user_role comment '用户角色对应表';

alter table role_sysresource add constraint FK_ROLE_SYS_REFERENCE_tb_role foreign key (ROLEID)
      references tb_role (ID) on delete restrict on update restrict;

alter table tb_sysresource_item add constraint FK_TB_SYSRE_REFERENCE_TB_SYSRE foreign key (SUPERIORRESOURCE)
      references tb_sysresource (ID) on delete restrict on update restrict;

alter table tb_sysresource_item add constraint FK_TB_SYSRE_SUB_REF_TB_SYSRE foreign key (SUBORDINATERESOURCE)
      references tb_sysresource (ID) on delete restrict on update restrict;

alter table user_depart add constraint FK_USER_DEP_REFERENCE_TB_DEPAR foreign key (DEPARTID)
      references tb_depart (ID) on delete restrict on update restrict;

alter table user_depart add constraint FK_USER_DEP_REFERENCE_tb_user foreign key (USERID)
      references tb_user (ID) on delete restrict on update restrict;

alter table user_jobs add constraint JOBSSID foreign key (JOBID)
      references tb_jobs (ID) on delete restrict on update restrict;

alter table user_jobs add constraint USERSID foreign key (USERID)
      references tb_user (ID) on delete restrict on update restrict;

alter table user_role add constraint FK_USER_ROL_REFERENCE_tb_role foreign key (ROLEID)
      references tb_role (ID) on delete restrict on update restrict;

alter table user_role add constraint FK_USER_ROL_REFERENCE_tb_user foreign key (USERID)
      references tb_user (ID) on delete restrict on update restrict;



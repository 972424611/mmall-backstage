CREATE DATABASE mmall DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

create table sys_acl
(
  id            int unsigned auto_increment
  comment '权限id'
    primary key,
  code          varchar(20) default ''             not null
  comment '权限码',
  name          varchar(20) default ''             not null
  comment '权限名称',
  acl_module_id int default '0'                    not null
  comment '权限所在的权限模块id',
  url           varchar(100) default ''            not null
  comment '请求的url, 可以填正则表达式',
  type          int default '3'                    not null
  comment '类型, 1: 菜单, 2: 按钮, 3: 其他',
  status        int default '1'                    not null
  comment '状态, 1: 正常, 0: 冻结',
  seq           int default '0'                    not null
  comment '权限在当前模块下的顺序, 有小到大',
  remark        varchar(200) default ''            null
  comment '备注',
  operator      varchar(20) default ''             not null
  comment '操作人',
  operate_time  datetime default CURRENT_TIMESTAMP not null
  comment '最后一次更新时间',
  operate_ip    varchar(20) default ''             not null
  comment '最后一个更新者的ip地址'
);

create table sys_acl_module
(
  id           int unsigned auto_increment
  comment '权限模块id'
    primary key,
  name         varchar(20) default ''             not null
  comment '权限模块名称',
  parent_id    int(10) default '0'                not null
  comment '上级权限模块id',
  level        varchar(200) default ''            not null
  comment '权限模块层级',
  seq          int(10) default '0'                not null
  comment '权限模块在当前层级下的顺序, 由小到达',
  status       int default '1'                    not null
  comment '状态, 1: 正常, 0: 冻结',
  remark       varchar(200)                       null
  comment '备注',
  operator     varchar(20) default ''             not null
  comment '操作人',
  operate_time datetime default CURRENT_TIMESTAMP not null
  comment '最后一次操作时间',
  operate_ip   varchar(20) default ''             not null
  comment '最后一次更新操作者的ip地址'
);

create table sys_dept
(
  id           int unsigned auto_increment
  comment '部门id'
    primary key,
  name         varchar(20) default ''             not null
  comment '部门名称',
  parent_id    int(10) default '0'                not null
  comment '上级部门id',
  level        varchar(200) default ''            not null
  comment '部门层级',
  seq          int(10) default '0'                not null
  comment '部门在当前层级下的顺序, 由小到达',
  remark       varchar(200)                       null
  comment '备注',
  operator     varchar(20) default ''             not null
  comment '操作人',
  operate_time datetime default CURRENT_TIMESTAMP not null
  comment '最后一次操作时间',
  operate_ip   varchar(20) default ''             not null
  comment '最后一次更新操作者的ip地址'
);

create table sys_log
(
  id           int unsigned auto_increment
    primary key,
  type         int default '0'                    not null
  comment '权限更新的类型, 1: 部门, 2: 用户, 3: 权限模块, 4: 权限, 5: 角色, 6: 角色用户关系, 7: 角色权限关系',
  target_id    int                                not null
  comment '基于type后指定的对象id, 比如用户, 权限, 角色等表的主键',
  old_value    text                               null
  comment '旧值',
  new_value    text                               null
  comment '新值',
  operator     varchar(20) default ''             not null
  comment '操作者',
  operate_time datetime default CURRENT_TIMESTAMP not null
  comment '最后一次更新时间',
  operate_ip   varchar(20) default ''             not null
  comment '最后一次更新者的ip地址',
  status       int default '0'                    not null
  comment '当前是否复原过, 0: 没有, 1: 复原过'
);

create table sys_role
(
  id           int unsigned auto_increment
  comment '角色id'
    primary key,
  name         varchar(20) default ''             not null
  comment '角色名称',
  type         int default '1'                    not null
  comment '角色的类型, 1: 管理员角色, 2: 其他',
  status       int default '1'                    not null
  comment '状态, 1: 可用, 0: 冻结',
  remark       varchar(200) default ''            null
  comment '备注',
  operator     varchar(20) default ''             not null
  comment '操作者',
  operate_time datetime default CURRENT_TIMESTAMP not null
  comment '最后一次更新的时间',
  operate_ip   varchar(20) default ''             not null
  comment '最后一次更新者的ip地址'
);

create table sys_role_acl
(
  id           int unsigned auto_increment
    primary key,
  role_id      int                                not null
  comment '角色id',
  acl_id       int                                not null
  comment '权限id',
  operator     varchar(20) default ''             not null
  comment '操作者',
  operate_time datetime default CURRENT_TIMESTAMP not null
  comment '最后一次更新时间',
  operate_ip   varchar(200) default ''            not null
  comment '最后一次更新者ip地址'
);

create table sys_role_user
(
  id           int unsigned auto_increment
    primary key,
  role_id      int                                not null
  comment '角色id',
  user_id      int                                not null
  comment '用户id',
  operator     varchar(20) default ''             not null
  comment '操作者',
  operate_time datetime default CURRENT_TIMESTAMP not null
  comment '最后一次更新的时间',
  operate_ip   varchar(20) default ''             not null
  comment '最后一次更新者的ip地址'
);

create table sys_user
(
  id           int unsigned auto_increment
  comment '用户id'
    primary key,
  username     varchar(20) default ''             not null
  comment '用户名称',
  telephone    varchar(13) default ''             not null
  comment '手机号',
  mail         varchar(50) default ''             not null
  comment '邮箱',
  password     varchar(64) default ''             not null
  comment '加密后的密码',
  dept_id      int default '0'                    not null
  comment '用户所在部门id',
  status       int default '1'                    not null
  comment '状态, 1: 正常, 0: 冻结状态, 2: 删除',
  remark       varchar(200) default ''            null
  comment '备注',
  operator     varchar(20) default ''             not null
  comment '操作者',
  operate_time datetime default CURRENT_TIMESTAMP not null
  comment '最后一次更新时间',
  operate_ip   varchar(20) default ''             not null
  comment '最后一次更新者ip'
);


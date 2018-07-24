# ProjectCode
本项目含义：开发的项目代码；
接口功能：查询出带有省、市、区的数据，三层循环输出 json 格式数据；
表结构如下：
CREATE TABLE `cy_geographical_structure` (
  `ID` int(55) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `STRUCTURE_CODE` varchar(55) DEFAULT NULL COMMENT '地理编号',
  `NAME` varchar(55) DEFAULT NULL COMMENT '地理名称',
  `DESCRIPTION` text COMMENT '地理描述',
  `STRUCTURE_LOGO` varchar(555) DEFAULT NULL COMMENT '地理LOGO',
  `PARENTID` int(55) DEFAULT '-1' COMMENT '地理父ID',
  `SORT` int(55) DEFAULT '0',
  `IS_DEL` int(11) DEFAULT '0' COMMENT '是否删除0:不删除，1：删除',
  `CREATE_BY` varchar(55) DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_BY` varchar(55) DEFAULT NULL COMMENT '更新人',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `BID` int(55) DEFAULT NULL COMMENT '所对应的kz_business主键',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='地理结构信息';



/*
 Navicat Premium Data Transfer

 Source Server         : Local
 Source Server Type    : MySQL
 Source Server Version : 50540
 Source Host           : localhost:3306
 Source Schema         : pressure_measure

 Target Server Type    : MySQL
 Target Server Version : 50540
 File Encoding         : 65001

 Date: 26/05/2024 23:25:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for aggregate_report
-- ----------------------------
DROP TABLE IF EXISTS `aggregate_report`;
CREATE TABLE `aggregate_report`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `plan_id` int(11) NULL DEFAULT NULL,
  `samples_num` int(11) NULL DEFAULT NULL,
  `average` double NULL DEFAULT NULL,
  `median` double NULL DEFAULT NULL,
  `min` double NULL DEFAULT NULL,
  `max` double NULL DEFAULT NULL,
  `p90` double NULL DEFAULT NULL,
  `p95` double NULL DEFAULT NULL,
  `p99` double NULL DEFAULT NULL,
  `tps` double NULL DEFAULT NULL,
  `error_rate` double NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for constant_timer
-- ----------------------------
DROP TABLE IF EXISTS `constant_timer`;
CREATE TABLE `constant_timer`  (
  `id` int(11) NOT NULL COMMENT '定时器id',
  `timer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '定时器名称',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注释',
  `thread_delay` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '线程延迟时间(ms)',
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `fk_constant_timer_timer` FOREIGN KEY (`id`) REFERENCES `timer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for cookie_manager
-- ----------------------------
DROP TABLE IF EXISTS `cookie_manager`;
CREATE TABLE `cookie_manager`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'cookie管理器id',
  `cookie_manager_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cookie管理器名称',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注释',
  `cookies` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cookies',
  `clear_each_iteration` bit(1) NOT NULL COMMENT '每次迭代是否清除cookie',
  `thread_group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_cookie_manager_thread_group`(`thread_group_id`) USING BTREE,
  CONSTRAINT `fk_cookie_manager_thread_group` FOREIGN KEY (`thread_group_id`) REFERENCES `thread_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gaussian_random_timer
-- ----------------------------
DROP TABLE IF EXISTS `gaussian_random_timer`;
CREATE TABLE `gaussian_random_timer`  (
  `id` int(11) NOT NULL COMMENT '定时器id',
  `timer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '定时器名称',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注释',
  `deviation` bigint(20) NOT NULL COMMENT '偏差(ms)',
  `constant_delay_offset` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '固定延迟(ms)',
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `fk_gaussian_random_timer_timer` FOREIGN KEY (`id`) REFERENCES `timer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for header_manager
-- ----------------------------
DROP TABLE IF EXISTS `header_manager`;
CREATE TABLE `header_manager`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'http请求信息头管理器id',
  `header_manager_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'http请求信息头管理器名称',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注释',
  `headers` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求信息头键值对',
  `thread_group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_header_manager_thread_group`(`thread_group_id`) USING BTREE,
  CONSTRAINT `fk_header_manager_thread_group` FOREIGN KEY (`thread_group_id`) REFERENCES `thread_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for http_sampler_proxy
-- ----------------------------
DROP TABLE IF EXISTS `http_sampler_proxy`;
CREATE TABLE `http_sampler_proxy`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'http请求取样器id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'http请求取样器名称',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注释',
  `protocol` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'http请求协议，如https、http',
  `server` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务器名称或IP地址',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求路径',
  `port` int(11) NOT NULL COMMENT '端口',
  `method` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求方法',
  `content_encoding` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容编码',
  `use_keep_alive` bit(1) NULL DEFAULT b'1' COMMENT '是否使用保持连接',
  `follow_redirects` bit(1) NULL DEFAULT b'1' COMMENT '是否开启跟随重定向',
  `auto_redirects` bit(1) NULL DEFAULT b'0' COMMENT '是否开启自动重定向',
  `arguments` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求参数键值对',
  `thread_group_id` int(11) NOT NULL COMMENT '所属线程组id',
  `body` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求体',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_http_sampler_proxy_thread_group`(`thread_group_id`) USING BTREE,
  CONSTRAINT `fk_http_sampler_proxy_thread_group` FOREIGN KEY (`thread_group_id`) REFERENCES `thread_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for loop_controller
-- ----------------------------
DROP TABLE IF EXISTS `loop_controller`;
CREATE TABLE `loop_controller`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `loop_num` int(11) NOT NULL,
  `continue_forever` bit(1) NOT NULL,
  `thread_group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `loop_controller___fk_thread_group`(`thread_group_id`) USING BTREE,
  CONSTRAINT `loop_controller___fk_thread_group` FOREIGN KEY (`thread_group_id`) REFERENCES `thread_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '循环控制器' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for poisson_random_timer
-- ----------------------------
DROP TABLE IF EXISTS `poisson_random_timer`;
CREATE TABLE `poisson_random_timer`  (
  `id` int(11) NOT NULL COMMENT '定时器id',
  `timer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '定时器名称',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注释',
  `lambda` bigint(20) NOT NULL COMMENT 'lambda(ms)',
  `constant_delay_offset` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '固定延迟(ms)',
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `fk_poissonrandomtimer_timer` FOREIGN KEY (`id`) REFERENCES `timer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for test_plan
-- ----------------------------
DROP TABLE IF EXISTS `test_plan`;
CREATE TABLE `test_plan`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '测试计划id',
  `test_plan_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试计划名称',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注释',
  `serialized` bit(1) NOT NULL COMMENT '是否独立运行每个线程组，如在一个组运行结束后启动下一个',
  `functional_mode` bit(1) NOT NULL COMMENT '是否开启函数测试模式',
  `tear_down` bit(1) NOT NULL COMMENT '主线程结束后是否允许tear down线程组',
  `status` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试计划状态，已创建，运行中，执行完毕',
  `namespace` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `pod_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for test_result
-- ----------------------------
DROP TABLE IF EXISTS `test_result`;
CREATE TABLE `test_result`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `plan_id` int(11) NULL DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `start_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  `pause_time` bigint(20) NULL DEFAULT NULL,
  `idle_time` bigint(20) NULL DEFAULT NULL,
  `latency` bigint(20) NULL DEFAULT NULL,
  `success` int(11) NULL DEFAULT NULL,
  `connect_time` bigint(20) NULL DEFAULT NULL,
  `response_code` int(11) NULL DEFAULT NULL,
  `response_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `response_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `response_headers` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `bytes` bigint(20) NULL DEFAULT NULL,
  `headers_size` int(11) NULL DEFAULT NULL,
  `body_size` int(11) NULL DEFAULT NULL,
  `sent_bytes` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for thread_group
-- ----------------------------
DROP TABLE IF EXISTS `thread_group`;
CREATE TABLE `thread_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '线程组id',
  `test_plan_id` int(11) NOT NULL COMMENT '所属的测试计划id',
  `thread_group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '线程组名称',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注释',
  `thread_num` int(11) NOT NULL COMMENT '线程数量',
  `ramp_up` bigint(20) NOT NULL COMMENT 'ramp up时间(s)',
  `delay` bigint(20) NULL DEFAULT NULL COMMENT '延迟时间(s)',
  `scheduler` bit(1) NOT NULL COMMENT '是否开启调度器',
  `duration` int(11) NULL DEFAULT NULL COMMENT '持续时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_thread_group_test_plan`(`test_plan_id`) USING BTREE,
  CONSTRAINT `fk_thread_group_test_plan` FOREIGN KEY (`test_plan_id`) REFERENCES `test_plan` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for timer
-- ----------------------------
DROP TABLE IF EXISTS `timer`;
CREATE TABLE `timer`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NULL DEFAULT NULL COMMENT '1-ConstantTimer, 2-UniformRandomTimer, 3-GaussianRandomTimer, 4-PoissonRandomTimer',
  `thread_group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_timer_thread_group`(`thread_group_id`) USING BTREE,
  CONSTRAINT `fk_timer_thread_group` FOREIGN KEY (`thread_group_id`) REFERENCES `thread_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for uniform_random_timer
-- ----------------------------
DROP TABLE IF EXISTS `uniform_random_timer`;
CREATE TABLE `uniform_random_timer`  (
  `id` int(11) NOT NULL,
  `timer_name` timestamp NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `random_delay_maximum` bigint(20) NULL DEFAULT NULL,
  `constant_delay_offset` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `fk_uniform_random_timer_timer` FOREIGN KEY (`id`) REFERENCES `timer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;

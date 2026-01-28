/*
 Navicat Premium Dump SQL

 Source Server         : conn
 Source Server Type    : MySQL
 Source Server Version : 100432 (10.4.32-MariaDB)
 Source Host           : localhost:3306
 Source Schema         : chodientu_db

 Target Server Type    : MySQL
 Target Server Version : 100432 (10.4.32-MariaDB)
 File Encoding         : 65001

 Date: 25/01/2026 02:24:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, 'dien thoai', '\"\"');
INSERT INTO `categories` VALUES (2, 'RAM', '\"\"');
INSERT INTO `categories` VALUES (3, 'SSD', '\"\"');

-- ----------------------------
-- Table structure for chats
-- ----------------------------
DROP TABLE IF EXISTS `chats`;
CREATE TABLE `chats`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `room_id` int NULL DEFAULT NULL,
  `sender_id` int NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `create_at` timestamp NULL DEFAULT NULL,
  `product_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_room_id_room`(`room_id` ASC) USING BTREE,
  INDEX `fk_product_id_product`(`product_id` ASC) USING BTREE,
  INDEX `fk_sender_id_room`(`sender_id` ASC) USING BTREE,
  CONSTRAINT `fk_product_id_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_room_id_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sender_id_room` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 357 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chats
-- ----------------------------
INSERT INTO `chats` VALUES (1, 1, 3, 'xin chao mon hang nay con khong a', '2026-01-19 09:30:18', 1);
INSERT INTO `chats` VALUES (2, 1, 3, 'xin chao mon hang nay con khong a', '2026-01-19 09:31:37', 1);
INSERT INTO `chats` VALUES (3, 1, 3, 'xin chao mon hang nay con khong a', '2026-01-19 09:32:22', 1);
INSERT INTO `chats` VALUES (5, 1, 2, 'xin chao mon hang nay con khong a', '2026-01-19 09:36:52', 1);
INSERT INTO `chats` VALUES (6, 2, 1, 'xin chao mon hang nay con khong a', '2026-01-19 09:40:27', 1);
INSERT INTO `chats` VALUES (7, 2, 1, 'xin chao mon hang nay con khong a', '2026-01-19 09:43:33', 1);
INSERT INTO `chats` VALUES (8, 2, 1, 'xin chao mon hang nay con khong a', '2026-01-19 09:47:26', 1);
INSERT INTO `chats` VALUES (9, 2, 1, 'xin chao mon hang nay con khong a', '2026-01-19 09:47:33', 1);
INSERT INTO `chats` VALUES (10, 2, 1, 'xin chao mon hang nay con khong a', '2026-01-19 09:54:19', 1);
INSERT INTO `chats` VALUES (11, 2, 1, 'xin chao mon hang nay con khong a', '2026-01-19 09:55:43', 1);
INSERT INTO `chats` VALUES (12, 2, 1, 'xin chao ban minh la a', '2026-01-19 09:56:42', 1);
INSERT INTO `chats` VALUES (13, 2, 1, 'xin alo 1234', '2026-01-19 09:56:56', 1);
INSERT INTO `chats` VALUES (14, 2, 1, 'xin alo 1234', '2026-01-19 09:58:20', 1);
INSERT INTO `chats` VALUES (15, 2, 1, 'xin chao ban la ai', '2026-01-19 09:58:33', 1);
INSERT INTO `chats` VALUES (16, 2, 1, 'xin chao chao xin', '2026-01-19 09:59:05', 1);
INSERT INTO `chats` VALUES (17, 2, 1, 'xin chao chao xin', '2026-01-19 09:59:28', 1);
INSERT INTO `chats` VALUES (18, 2, 1, 'xin chao chao xin', '2026-01-19 09:59:37', 1);
INSERT INTO `chats` VALUES (19, 2, 1, 'xin chao chao xin', '2026-01-19 09:59:43', 1);
INSERT INTO `chats` VALUES (20, 2, 1, 'xin chao chao xin', '2026-01-19 10:00:09', 1);
INSERT INTO `chats` VALUES (21, 2, 1, 'xin chao chao xin', '2026-01-19 10:01:00', 1);
INSERT INTO `chats` VALUES (22, 2, 1, 'xin chao chao xin', '2026-01-19 10:02:14', 1);
INSERT INTO `chats` VALUES (23, 2, 1, 'xin chao chao xin', '2026-01-19 10:02:21', 1);
INSERT INTO `chats` VALUES (24, 1, 2, 'xin chao chao xin', '2026-01-19 10:03:33', 1);
INSERT INTO `chats` VALUES (25, 1, 2, 'xin chao chao xin', '2026-01-19 10:04:46', 1);
INSERT INTO `chats` VALUES (26, 1, 2, 'xin chao ban nha', '2026-01-19 10:05:02', 1);
INSERT INTO `chats` VALUES (27, 1, 2, 'co chuyen gi khong', '2026-01-19 10:08:12', 1);
INSERT INTO `chats` VALUES (28, 1, 2, 'co chuyen gi khong', '2026-01-19 10:12:42', 1);
INSERT INTO `chats` VALUES (29, 1, 2, 'tetst lan 1', '2026-01-19 10:12:56', 1);
INSERT INTO `chats` VALUES (30, 1, 2, 'tetst lan 2', '2026-01-19 10:17:31', 1);
INSERT INTO `chats` VALUES (31, 1, 2, 'tetst lan 2', '2026-01-19 10:32:20', 1);
INSERT INTO `chats` VALUES (32, 1, 2, 'tetst lan 2', '2026-01-19 10:32:26', 1);
INSERT INTO `chats` VALUES (33, 1, 2, 'tetst lan 3', '2026-01-19 10:39:55', 1);
INSERT INTO `chats` VALUES (34, 1, 2, 'tetst lan 4', '2026-01-19 10:41:20', 1);
INSERT INTO `chats` VALUES (35, 1, 2, 'tetst lan 4', '2026-01-19 10:43:39', 1);
INSERT INTO `chats` VALUES (36, 1, 2, 'tetst lan 4', '2026-01-19 10:43:51', 1);
INSERT INTO `chats` VALUES (37, 1, 2, 'tetst lan 5=', '2026-01-19 10:44:10', 1);
INSERT INTO `chats` VALUES (38, 1, 2, 'tetst lan 5=', '2026-01-19 10:46:41', 1);
INSERT INTO `chats` VALUES (39, 1, 2, 'tetst lan 5=', '2026-01-19 10:49:48', 1);
INSERT INTO `chats` VALUES (40, 1, 2, 'tetst lan 5=', '2026-01-19 10:50:00', 1);
INSERT INTO `chats` VALUES (41, 1, 2, 'tetst lan 5=', '2026-01-19 10:50:08', 1);
INSERT INTO `chats` VALUES (42, 1, 2, 'tetst lan 6', '2026-01-19 10:50:35', 1);
INSERT INTO `chats` VALUES (43, 1, 2, 'tetst lan 7', '2026-01-19 10:57:06', 1);
INSERT INTO `chats` VALUES (44, 1, 2, 'tetst lan 7', '2026-01-19 10:57:13', 1);
INSERT INTO `chats` VALUES (45, 1, 2, 'tetst lan 8', '2026-01-19 10:58:53', 1);
INSERT INTO `chats` VALUES (46, 1, 2, 'tetst lan 9', '2026-01-19 10:59:09', 1);
INSERT INTO `chats` VALUES (47, 1, 2, 'tetst lan 9', '2026-01-19 11:51:23', 1);
INSERT INTO `chats` VALUES (48, 1, 2, 'tetst lan 10', '2026-01-19 11:51:49', 1);
INSERT INTO `chats` VALUES (49, 1, 2, '123123', '2026-01-19 12:22:42', 1);
INSERT INTO `chats` VALUES (50, 1, 2, '123123', '2026-01-19 12:27:41', 1);
INSERT INTO `chats` VALUES (51, 1, 2, 'asdas', '2026-01-19 12:27:45', 1);
INSERT INTO `chats` VALUES (52, 1, 2, '21231', '2026-01-19 12:33:40', 1);
INSERT INTO `chats` VALUES (53, 1, 3, 'xin chao chao â', '2026-01-19 12:36:46', 1);
INSERT INTO `chats` VALUES (54, 1, 2, 'tetst lan 10', '2026-01-19 12:37:02', 1);
INSERT INTO `chats` VALUES (55, 1, 3, 'chfaa à á', '2026-01-19 12:37:17', 1);
INSERT INTO `chats` VALUES (56, 1, 2, 'tetst lan 11', '2026-01-19 12:38:40', 1);
INSERT INTO `chats` VALUES (57, 1, 2, 'tetst lan 12', '2026-01-19 12:38:52', 1);
INSERT INTO `chats` VALUES (58, 1, 2, 'tetst lan 14', '2026-01-19 12:39:14', 1);
INSERT INTO `chats` VALUES (59, 1, 3, '123123123', '2026-01-19 12:47:32', 1);
INSERT INTO `chats` VALUES (60, 1, 2, 'tetst lan 14', '2026-01-19 12:48:00', 1);
INSERT INTO `chats` VALUES (61, 1, 2, 'tetst lan 15', '2026-01-19 12:48:11', 1);
INSERT INTO `chats` VALUES (62, 1, 3, '123123123', '2026-01-19 12:48:30', 1);
INSERT INTO `chats` VALUES (63, 1, 3, 'xin chao 1234', '2026-01-19 12:49:20', 1);
INSERT INTO `chats` VALUES (64, 1, 3, 'ffu', '2026-01-19 12:51:07', 1);
INSERT INTO `chats` VALUES (65, 1, 2, 'tetst lan 15', '2026-01-19 12:53:54', 1);
INSERT INTO `chats` VALUES (66, 1, 3, 'ttt nguyen uy khanh', '2026-01-19 12:55:39', 1);
INSERT INTO `chats` VALUES (67, 1, 3, 'đâsdaasd', '2026-01-19 13:11:01', 1);
INSERT INTO `chats` VALUES (68, 1, 3, 'rết lên mặt ', '2026-01-19 13:21:12', 1);
INSERT INTO `chats` VALUES (69, 1, 3, 'updateFCMToken', '2026-01-19 13:23:39', 1);
INSERT INTO `chats` VALUES (70, 1, 3, 'hello world', '2026-01-19 13:38:47', 1);
INSERT INTO `chats` VALUES (71, 1, 3, 'what your name\n', '2026-01-19 13:39:00', 1);
INSERT INTO `chats` VALUES (72, 1, 2, 'tetst lan 15', '2026-01-19 13:41:32', 1);
INSERT INTO `chats` VALUES (73, 1, 3, 'waht í your name', '2026-01-19 13:41:56', 1);
INSERT INTO `chats` VALUES (74, 1, 3, 'hi', '2026-01-19 13:47:06', 1);
INSERT INTO `chats` VALUES (75, 1, 3, 'how ảe you', '2026-01-19 13:47:25', 1);
INSERT INTO `chats` VALUES (76, 1, 3, 'hi\n', '2026-01-19 13:48:00', 1);
INSERT INTO `chats` VALUES (77, 1, 2, 'tetst lan 15', '2026-01-19 13:48:25', 1);
INSERT INTO `chats` VALUES (78, 1, 3, 'tetst lan 15', '2026-01-19 13:56:05', 1);
INSERT INTO `chats` VALUES (79, 1, 3, 'tetst lan 15', '2026-01-19 14:02:48', 1);
INSERT INTO `chats` VALUES (80, 1, 2, 'tetst lan 15', '2026-01-19 14:02:57', 1);
INSERT INTO `chats` VALUES (81, 1, 2, 'tetst lan 16', '2026-01-19 14:03:06', 1);
INSERT INTO `chats` VALUES (82, 1, 3, 'tetst lan 16', '2026-01-19 14:08:51', 1);
INSERT INTO `chats` VALUES (83, 1, 3, '123123', '2026-01-19 14:09:14', 1);
INSERT INTO `chats` VALUES (84, 1, 3, '123123', '2026-01-19 14:09:35', 1);
INSERT INTO `chats` VALUES (85, 1, 3, 'tetst lan 16', '2026-01-19 14:10:26', 1);
INSERT INTO `chats` VALUES (86, 1, 3, 'tetst lan 17', '2026-01-19 14:10:49', 1);
INSERT INTO `chats` VALUES (87, 1, 2, 'tetst lan 17', '2026-01-19 14:11:39', 1);
INSERT INTO `chats` VALUES (88, 1, 3, 'qweqweasd', '2026-01-19 14:13:10', 1);
INSERT INTO `chats` VALUES (89, 1, 2, 'tetst lan 17', '2026-01-19 14:13:25', 1);
INSERT INTO `chats` VALUES (90, 1, 2, 'tetst lan 17', '2026-01-19 14:13:39', 1);
INSERT INTO `chats` VALUES (91, 1, 3, 'tetst lan 17', '2026-01-19 14:13:57', 1);
INSERT INTO `chats` VALUES (92, 1, 3, 'day la atoi', '2026-01-19 14:14:30', 1);
INSERT INTO `chats` VALUES (93, 1, 3, 'day la toi', '2026-01-19 14:14:48', 1);
INSERT INTO `chats` VALUES (94, 1, 3, 'day la toi', '2026-01-19 14:14:52', 1);
INSERT INTO `chats` VALUES (95, 1, 2, 'day la toi', '2026-01-19 14:15:16', 1);
INSERT INTO `chats` VALUES (96, 1, 2, 'day la toi', '2026-01-19 14:16:09', 1);
INSERT INTO `chats` VALUES (97, 1, 3, 'day la toi', '2026-01-19 14:16:17', 1);
INSERT INTO `chats` VALUES (98, 1, 3, 'day la toi', '2026-01-19 14:16:22', 1);
INSERT INTO `chats` VALUES (99, 1, 2, 'day la toi', '2026-01-19 14:16:31', 1);
INSERT INTO `chats` VALUES (100, 1, 2, 'day la toi', '2026-01-19 14:16:36', 1);
INSERT INTO `chats` VALUES (101, 1, 2, 'day la toi', '2026-01-19 14:16:40', 1);
INSERT INTO `chats` VALUES (102, 1, 3, 'hi xin chao ban', '2026-01-19 14:17:07', 1);
INSERT INTO `chats` VALUES (103, 1, 2, 'o chao ban nha', '2026-01-19 14:17:21', 1);
INSERT INTO `chats` VALUES (104, 1, 3, 'nam náy ban bao nhieu tuoi roi', '2026-01-19 14:24:34', 1);
INSERT INTO `chats` VALUES (105, 1, 2, 'toi 10 tuoi con ban', '2026-01-19 14:25:13', 1);
INSERT INTO `chats` VALUES (106, 1, 2, 'ban bao nhieu tuoi roi', '2026-01-19 14:25:39', 1);
INSERT INTO `chats` VALUES (107, 1, 3, 'toi cung muoi tuoi roi', '2026-01-19 14:25:57', 1);
INSERT INTO `chats` VALUES (108, 1, 3, 'nam nay la lop 10 phai khong', '2026-01-19 14:26:18', 1);
INSERT INTO `chats` VALUES (109, 1, 3, 'hay la 11', '2026-01-19 14:26:45', 1);
INSERT INTO `chats` VALUES (110, 1, 3, 'hay la 12', '2026-01-19 14:26:55', 1);
INSERT INTO `chats` VALUES (111, 1, 3, '13 ha', '2026-01-19 14:27:07', 1);
INSERT INTO `chats` VALUES (112, 1, 3, '14 tuoi', '2026-01-19 14:27:57', 1);
INSERT INTO `chats` VALUES (113, 1, 2, 'khong co lop 13 hay 14 dau ban oi', '2026-01-19 14:29:05', 1);
INSERT INTO `chats` VALUES (114, 1, 2, 'ma la lop 15 hahahaa haaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '2026-01-19 14:29:31', 1);
INSERT INTO `chats` VALUES (115, 1, 2, 'ma la lop 15 hahahaa haaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '2026-01-19 14:29:40', 1);
INSERT INTO `chats` VALUES (116, 1, 2, 'bat ngo lam chu gi', '2026-01-19 14:30:38', 1);
INSERT INTO `chats` VALUES (117, 1, 3, 'cin chao', '2026-01-19 14:33:06', 1);
INSERT INTO `chats` VALUES (118, 1, 2, 'queeeee', '2026-01-19 14:33:35', 1);
INSERT INTO `chats` VALUES (119, 1, 3, 'hello', '2026-01-19 14:42:37', 1);
INSERT INTO `chats` VALUES (120, 1, 3, 'alo ola', '2026-01-19 14:42:52', 1);
INSERT INTO `chats` VALUES (121, 1, 2, 'hello', '2026-01-19 14:44:33', 1);
INSERT INTO `chats` VALUES (122, 1, 2, 'xin chào', '2026-01-19 14:44:50', 1);
INSERT INTO `chats` VALUES (123, 1, 2, 'tôi chao xìn', '2026-01-19 14:45:39', 1);
INSERT INTO `chats` VALUES (124, 1, 2, 'hú', '2026-01-19 14:45:54', 1);
INSERT INTO `chats` VALUES (125, 1, 2, 'he', '2026-01-19 14:45:57', 1);
INSERT INTO `chats` VALUES (126, 1, 2, 'khà khá', '2026-01-19 14:46:33', 1);
INSERT INTO `chats` VALUES (127, 1, 3, 'khong chao nha', '2026-01-19 14:46:41', 1);
INSERT INTO `chats` VALUES (128, 1, 2, 'ô', '2026-01-19 14:46:47', 1);
INSERT INTO `chats` VALUES (129, 1, 3, 'leu leu', '2026-01-19 14:46:48', 1);
INSERT INTO `chats` VALUES (130, 1, 2, 'leu leu', '2026-01-19 14:46:53', 1);
INSERT INTO `chats` VALUES (131, 1, 2, 'them chức năng ghi âm đi bạn', '2026-01-19 14:47:06', 1);
INSERT INTO `chats` VALUES (132, 1, 3, 'leu leu lue leu leu lue', '2026-01-19 14:47:10', 1);
INSERT INTO `chats` VALUES (133, 1, 2, 'tui đau bụng quqs', '2026-01-19 14:47:15', 1);
INSERT INTO `chats` VALUES (134, 1, 2, 'đợi tí', '2026-01-19 14:47:21', 1);
INSERT INTO `chats` VALUES (135, 1, 2, 'hết tuần này bạn dìa quê đk', '2026-01-19 14:47:34', 1);
INSERT INTO `chats` VALUES (136, 1, 3, 'khong biet nua', '2026-01-19 14:47:47', 1);
INSERT INTO `chats` VALUES (137, 1, 2, ':)))', '2026-01-19 14:48:04', 1);
INSERT INTO `chats` VALUES (138, 1, 3, 'ghi am thi khong lam kip :<<', '2026-01-19 14:48:08', 1);
INSERT INTO `chats` VALUES (139, 1, 2, 'ok mà dị thì giỏi quá trời ời', '2026-01-19 14:48:27', 1);
INSERT INTO `chats` VALUES (140, 1, 3, 'thanh kiu', '2026-01-19 14:50:47', 1);
INSERT INTO `chats` VALUES (141, 1, 3, 'cin chao', '2026-01-19 14:54:15', 1);
INSERT INTO `chats` VALUES (142, 1, 2, 'queeeee', '2026-01-19 14:54:36', 1);
INSERT INTO `chats` VALUES (143, 1, 2, 'hello', '2026-01-19 14:57:23', 1);
INSERT INTO `chats` VALUES (144, 1, 2, 'hi', '2026-01-19 14:57:24', 1);
INSERT INTO `chats` VALUES (145, 1, 2, 'hihi', '2026-01-19 14:57:29', 1);
INSERT INTO `chats` VALUES (146, 1, 2, 'ni hao', '2026-01-19 15:01:16', 1);
INSERT INTO `chats` VALUES (147, 1, 2, 'hello', '2026-01-19 15:09:37', 1);
INSERT INTO `chats` VALUES (148, 1, 2, 'quo', '2026-01-19 15:10:26', 1);
INSERT INTO `chats` VALUES (149, 1, 2, 'long', '2026-01-19 15:10:29', 1);
INSERT INTO `chats` VALUES (150, 1, 2, 'bay', '2026-01-19 15:10:31', 1);
INSERT INTO `chats` VALUES (151, 1, 3, 'tieng trung do\n', '2026-01-19 15:34:41', 1);
INSERT INTO `chats` VALUES (152, 1, 2, 'hege', '2026-01-19 15:35:47', 1);
INSERT INTO `chats` VALUES (153, 1, 2, 'queeeee', '2026-01-19 15:47:29', 1);
INSERT INTO `chats` VALUES (154, 1, 2, 'queeeee', '2026-01-19 15:47:33', 1);
INSERT INTO `chats` VALUES (155, 1, 2, 'queeeee', '2026-01-19 15:47:35', 1);
INSERT INTO `chats` VALUES (156, 1, 2, 'queeeee', '2026-01-19 15:47:42', 1);
INSERT INTO `chats` VALUES (157, 1, 2, 'queeeee', '2026-01-19 15:47:44', 1);
INSERT INTO `chats` VALUES (158, 1, 2, 'queeeee', '2026-01-19 15:47:45', 1);
INSERT INTO `chats` VALUES (159, 1, 2, 'queeeee', '2026-01-19 15:48:01', 1);
INSERT INTO `chats` VALUES (160, 1, 2, 'queeeee', '2026-01-19 16:04:16', 1);
INSERT INTO `chats` VALUES (161, 1, 2, 'queeeee', '2026-01-19 16:04:17', 1);
INSERT INTO `chats` VALUES (162, 1, 2, 'queeeee', '2026-01-19 16:04:18', 1);
INSERT INTO `chats` VALUES (163, 1, 2, 'queeeee', '2026-01-19 16:04:20', 1);
INSERT INTO `chats` VALUES (164, 1, 2, 'queeeee', '2026-01-19 16:05:49', 1);
INSERT INTO `chats` VALUES (165, 1, 2, 'queeeee', '2026-01-19 16:05:58', 1);
INSERT INTO `chats` VALUES (166, 1, 2, 'queeeee', '2026-01-19 16:06:00', 1);
INSERT INTO `chats` VALUES (167, 1, 2, 'queeeee', '2026-01-19 16:06:01', 1);
INSERT INTO `chats` VALUES (168, 1, 2, 'queeeee', '2026-01-19 16:07:51', 1);
INSERT INTO `chats` VALUES (169, 1, 2, 'queeeee', '2026-01-19 16:07:54', 1);
INSERT INTO `chats` VALUES (170, 1, 2, 'queeeee', '2026-01-19 16:07:58', 1);
INSERT INTO `chats` VALUES (171, 1, 2, 'queeeee', '2026-01-19 16:08:44', 1);
INSERT INTO `chats` VALUES (172, 1, 2, 'queeeee', '2026-01-19 16:08:48', 1);
INSERT INTO `chats` VALUES (173, 1, 2, 'queeeee', '2026-01-19 16:09:07', 1);
INSERT INTO `chats` VALUES (174, 1, 2, 'queeeee', '2026-01-19 16:11:31', 1);
INSERT INTO `chats` VALUES (175, 1, 2, 'queeeee', '2026-01-19 16:11:37', 1);
INSERT INTO `chats` VALUES (176, 1, 2, 'queeeee', '2026-01-19 16:11:42', 1);
INSERT INTO `chats` VALUES (177, 1, 2, 'queeeee', '2026-01-19 16:11:51', 1);
INSERT INTO `chats` VALUES (178, 1, 2, 'queeeee', '2026-01-19 16:11:57', 1);
INSERT INTO `chats` VALUES (179, 1, 2, 'queeeee', '2026-01-19 16:12:09', 1);
INSERT INTO `chats` VALUES (180, 1, 2, 'queeeee', '2026-01-19 16:12:31', 1);
INSERT INTO `chats` VALUES (181, 1, 2, 'queeeee', '2026-01-19 16:12:36', 1);
INSERT INTO `chats` VALUES (182, 1, 2, 'queeeee', '2026-01-19 16:12:38', 1);
INSERT INTO `chats` VALUES (183, 1, 2, 'queeeee', '2026-01-19 16:12:40', 1);
INSERT INTO `chats` VALUES (184, 1, 3, 'hello', '2026-01-19 16:13:06', 1);
INSERT INTO `chats` VALUES (185, 1, 3, 'ban la ai', '2026-01-19 16:13:11', 1);
INSERT INTO `chats` VALUES (186, 1, 3, 'toi la   ai ke toi', '2026-01-19 16:13:31', 1);
INSERT INTO `chats` VALUES (187, 1, 3, 'vay ha', '2026-01-19 16:13:38', 1);
INSERT INTO `chats` VALUES (188, 1, 3, 'shjs', '2026-01-19 16:16:38', 1);
INSERT INTO `chats` VALUES (189, 1, 2, 'queeeee', '2026-01-20 04:53:53', 1);
INSERT INTO `chats` VALUES (190, 1, 2, 'queeeee', '2026-01-20 04:54:07', 1);
INSERT INTO `chats` VALUES (191, 1, 2, 'queeeee', '2026-01-20 04:54:09', 1);
INSERT INTO `chats` VALUES (192, 1, 2, 'queeeee', '2026-01-20 04:54:59', 1);
INSERT INTO `chats` VALUES (193, 1, 2, 'queeeee', '2026-01-20 04:55:17', 1);
INSERT INTO `chats` VALUES (194, 1, 2, 'queeeee', '2026-01-20 04:57:39', 1);
INSERT INTO `chats` VALUES (195, 1, 2, 'queeeee', '2026-01-20 04:57:45', 1);
INSERT INTO `chats` VALUES (196, 1, 2, 'queeeee', '2026-01-20 04:57:47', 1);
INSERT INTO `chats` VALUES (197, 1, 2, 'queeeee', '2026-01-20 05:09:39', 1);
INSERT INTO `chats` VALUES (198, 1, 2, 'queeeee', '2026-01-20 05:09:45', 1);
INSERT INTO `chats` VALUES (199, 1, 2, 'queeeee', '2026-01-20 05:11:59', 1);
INSERT INTO `chats` VALUES (200, 1, 2, 'queeeee', '2026-01-20 05:12:01', 1);
INSERT INTO `chats` VALUES (201, 1, 2, 'tests 1', '2026-01-20 05:14:02', 1);
INSERT INTO `chats` VALUES (202, 1, 2, 'tests 1', '2026-01-20 05:14:06', 1);
INSERT INTO `chats` VALUES (203, 1, 2, 'tests 1', '2026-01-20 05:14:08', 1);
INSERT INTO `chats` VALUES (204, 1, 2, 'tests 1', '2026-01-20 05:30:26', 1);
INSERT INTO `chats` VALUES (205, 1, 2, 'tests 1', '2026-01-20 05:30:34', 1);
INSERT INTO `chats` VALUES (206, 1, 2, 'tests 1', '2026-01-20 05:30:47', 1);
INSERT INTO `chats` VALUES (207, 1, 2, 'tests 1', '2026-01-20 05:30:48', 1);
INSERT INTO `chats` VALUES (208, 1, 2, 'tests 2', '2026-01-20 05:31:09', 1);
INSERT INTO `chats` VALUES (209, 1, 2, 'tests 3', '2026-01-20 05:31:17', 1);
INSERT INTO `chats` VALUES (210, 1, 2, 'tests 4', '2026-01-20 05:31:22', 1);
INSERT INTO `chats` VALUES (211, 1, 2, 'tests 5', '2026-01-20 05:31:39', 1);
INSERT INTO `chats` VALUES (212, 1, 2, 'tests 6', '2026-01-20 05:31:42', 1);
INSERT INTO `chats` VALUES (213, 1, 2, 'tests 7', '2026-01-20 05:31:48', 1);
INSERT INTO `chats` VALUES (214, 1, 2, 'tests 8', '2026-01-20 05:32:10', 1);
INSERT INTO `chats` VALUES (215, 1, 2, 'tests 9', '2026-01-20 05:32:14', 1);
INSERT INTO `chats` VALUES (216, 1, 2, 'tests 10', '2026-01-20 05:32:19', 1);
INSERT INTO `chats` VALUES (217, 1, 2, 'tests 10', '2026-01-20 06:35:31', 1);
INSERT INTO `chats` VALUES (218, 1, 2, 'tests 11', '2026-01-20 06:35:36', 1);
INSERT INTO `chats` VALUES (219, 1, 2, 'tests 12', '2026-01-20 06:35:40', 1);
INSERT INTO `chats` VALUES (220, 1, 2, 'tests 14', '2026-01-20 06:36:02', 1);
INSERT INTO `chats` VALUES (221, 1, 2, 'tests 15', '2026-01-20 06:36:07', 1);
INSERT INTO `chats` VALUES (222, 1, 2, 'tests 16', '2026-01-20 06:36:11', 1);
INSERT INTO `chats` VALUES (223, 1, 2, 'tests 16', '2026-01-20 06:39:06', 1);
INSERT INTO `chats` VALUES (224, 1, 2, 'tests 17', '2026-01-20 06:39:10', 1);
INSERT INTO `chats` VALUES (225, 1, 2, 'tests 18', '2026-01-20 06:39:15', 1);
INSERT INTO `chats` VALUES (226, 1, 2, 'tests 19', '2026-01-20 06:39:37', 1);
INSERT INTO `chats` VALUES (227, 1, 2, 'tests 20', '2026-01-20 06:39:43', 1);
INSERT INTO `chats` VALUES (228, 1, 2, 'tests 20', '2026-01-20 06:40:04', 1);
INSERT INTO `chats` VALUES (229, 1, 2, 'tests 20', '2026-01-20 06:40:06', 1);
INSERT INTO `chats` VALUES (230, 1, 2, 'tests 21', '2026-01-20 06:40:40', 1);
INSERT INTO `chats` VALUES (231, 1, 2, 'tests 22', '2026-01-20 06:41:07', 1);
INSERT INTO `chats` VALUES (232, 1, 2, 'tests 22', '2026-01-20 06:54:41', 1);
INSERT INTO `chats` VALUES (233, 1, 2, 'tests 23', '2026-01-20 06:54:48', 1);
INSERT INTO `chats` VALUES (234, 1, 2, 'tests 24', '2026-01-20 06:54:52', 1);
INSERT INTO `chats` VALUES (235, 1, 2, 'tests 24', '2026-01-20 06:58:23', 1);
INSERT INTO `chats` VALUES (236, 1, 2, 'tests 25', '2026-01-20 06:58:27', 1);
INSERT INTO `chats` VALUES (237, 1, 2, 'tests 26', '2026-01-20 06:58:30', 1);
INSERT INTO `chats` VALUES (238, 1, 2, 'tests 27', '2026-01-20 06:58:45', 1);
INSERT INTO `chats` VALUES (239, 1, 2, 'tests 27', '2026-01-20 06:58:49', 1);
INSERT INTO `chats` VALUES (240, 1, 2, 'tests 28', '2026-01-20 06:58:51', 1);
INSERT INTO `chats` VALUES (241, 1, 2, 'tests 29', '2026-01-20 06:59:01', 1);
INSERT INTO `chats` VALUES (242, 1, 2, 'tests 30', '2026-01-20 06:59:06', 1);
INSERT INTO `chats` VALUES (243, 1, 2, 'tests 31', '2026-01-20 06:59:07', 1);
INSERT INTO `chats` VALUES (244, 1, 2, 'tests 33', '2026-01-20 06:59:19', 1);
INSERT INTO `chats` VALUES (245, 1, 2, 'tests 34', '2026-01-20 06:59:22', 1);
INSERT INTO `chats` VALUES (246, 1, 2, 'tests 35', '2026-01-20 06:59:24', 1);
INSERT INTO `chats` VALUES (247, 1, 2, 'tests 36', '2026-01-20 06:59:26', 1);
INSERT INTO `chats` VALUES (248, 1, 2, 'tests 37', '2026-01-20 06:59:28', 1);
INSERT INTO `chats` VALUES (249, 1, 2, 'tests 38', '2026-01-20 06:59:29', 1);
INSERT INTO `chats` VALUES (250, 1, 2, 'tests 39', '2026-01-20 06:59:46', 1);
INSERT INTO `chats` VALUES (251, 1, 2, 'tests 40', '2026-01-20 07:00:00', 1);
INSERT INTO `chats` VALUES (252, 1, 2, 'tests 41', '2026-01-20 07:00:02', 1);
INSERT INTO `chats` VALUES (253, 1, 2, 'tests 42', '2026-01-20 07:33:00', 1);
INSERT INTO `chats` VALUES (254, 1, 2, 'tests 43', '2026-01-20 07:33:05', 1);
INSERT INTO `chats` VALUES (255, 1, 2, 'tests 44', '2026-01-20 07:33:07', 1);
INSERT INTO `chats` VALUES (256, 1, 2, 'tests 45', '2026-01-20 07:33:21', 1);
INSERT INTO `chats` VALUES (257, 1, 2, 'tests 45', '2026-01-20 07:33:22', 1);
INSERT INTO `chats` VALUES (258, 1, 2, 'tests 46', '2026-01-20 07:33:24', 1);
INSERT INTO `chats` VALUES (259, 1, 2, 'tests 47', '2026-01-20 07:33:30', 1);
INSERT INTO `chats` VALUES (260, 1, 2, 'tests 48', '2026-01-20 07:49:28', 1);
INSERT INTO `chats` VALUES (261, 1, 2, 'tests 49', '2026-01-20 07:49:30', 1);
INSERT INTO `chats` VALUES (262, 1, 2, 'tests 49', '2026-01-20 07:49:33', 1);
INSERT INTO `chats` VALUES (263, 1, 2, 'tests 50', '2026-01-20 07:49:37', 1);
INSERT INTO `chats` VALUES (264, 1, 2, 'tests 51', '2026-01-20 07:49:57', 1);
INSERT INTO `chats` VALUES (265, 1, 2, 'tests 52', '2026-01-20 07:50:03', 1);
INSERT INTO `chats` VALUES (266, 1, 2, 'tests 53', '2026-01-20 07:50:12', 1);
INSERT INTO `chats` VALUES (267, 1, 2, 'tests 54', '2026-01-20 07:50:21', 1);
INSERT INTO `chats` VALUES (268, 1, 2, 'tests 54', '2026-01-20 10:00:49', 1);
INSERT INTO `chats` VALUES (269, 1, 2, 'tests 54', '2026-01-20 10:03:37', 1);
INSERT INTO `chats` VALUES (270, 1, 2, 'tests 55', '2026-01-20 10:03:44', 1);
INSERT INTO `chats` VALUES (271, 1, 2, 'tests 55', '2026-01-20 10:12:02', 1);
INSERT INTO `chats` VALUES (272, 1, 2, 'tests 55', '2026-01-20 10:12:28', 1);
INSERT INTO `chats` VALUES (273, 1, 2, 'tests 55', '2026-01-20 10:13:07', 1);
INSERT INTO `chats` VALUES (274, 1, 2, 'tests 55', '2026-01-20 10:17:32', 1);
INSERT INTO `chats` VALUES (275, 1, 2, 'tests 55', '2026-01-20 10:28:07', 1);
INSERT INTO `chats` VALUES (276, 1, 2, 'tests 55', '2026-01-20 10:31:45', 1);
INSERT INTO `chats` VALUES (277, 1, 2, 'tests 56', '2026-01-20 10:33:21', 1);
INSERT INTO `chats` VALUES (278, 1, 2, 'tests 56', '2026-01-20 10:35:12', 1);
INSERT INTO `chats` VALUES (279, 1, 2, 'tests 57', '2026-01-20 10:35:30', 1);
INSERT INTO `chats` VALUES (280, 1, 2, 'tests 58', '2026-01-20 10:44:54', 1);
INSERT INTO `chats` VALUES (281, 1, 3, 'rg', '2026-01-22 08:27:53', 1);
INSERT INTO `chats` VALUES (282, 1, 3, 'gdg', '2026-01-22 08:27:58', 1);
INSERT INTO `chats` VALUES (283, 1, 3, 'rerrr', '2026-01-22 08:28:02', 1);
INSERT INTO `chats` VALUES (284, 1, 2, 'tests 58', '2026-01-22 08:28:16', 1);
INSERT INTO `chats` VALUES (285, 1, 2, 'tests 58', '2026-01-22 08:28:55', 1);
INSERT INTO `chats` VALUES (286, 1, 2, 'tests 59', '2026-01-22 08:29:01', 1);
INSERT INTO `chats` VALUES (287, 1, 2, 'tests 59', '2026-01-22 08:29:17', 1);
INSERT INTO `chats` VALUES (288, 1, 2, 'tests 59', '2026-01-22 08:30:14', 1);
INSERT INTO `chats` VALUES (289, 1, 2, 'tests 59', '2026-01-22 08:31:57', 1);
INSERT INTO `chats` VALUES (290, 1, 2, 'tests 61', '2026-01-22 08:32:11', 1);
INSERT INTO `chats` VALUES (291, 1, 3, 'test62', '2026-01-22 08:37:38', 1);
INSERT INTO `chats` VALUES (292, 1, 2, 'tests 61', '2026-01-22 09:11:48', 1);
INSERT INTO `chats` VALUES (293, 1, 2, 'tests 62', '2026-01-22 09:12:00', 1);
INSERT INTO `chats` VALUES (294, 1, 3, 'TEST 63', '2026-01-22 09:12:25', 1);
INSERT INTO `chats` VALUES (295, 1, 3, '12312313', '2026-01-22 09:30:57', 1);
INSERT INTO `chats` VALUES (296, 1, 3, 'asdasda', '2026-01-22 10:32:22', 1);
INSERT INTO `chats` VALUES (297, 1, 2, 'tests 64', '2026-01-22 10:32:27', 1);
INSERT INTO `chats` VALUES (298, 1, 3, 'who are you', '2026-01-22 11:03:13', 1);
INSERT INTO `chats` VALUES (299, 1, 2, 'tests 65', '2026-01-22 11:03:46', 1);
INSERT INTO `chats` VALUES (300, 1, 3, 'xin chao xin chao 123123', '2026-01-22 11:22:07', 1);
INSERT INTO `chats` VALUES (301, 1, 3, 'xin chao xin chao 123123', '2026-01-22 11:22:55', 1);
INSERT INTO `chats` VALUES (302, 1, 3, '123123', '2026-01-22 13:18:47', 1);
INSERT INTO `chats` VALUES (303, 1, 2, 'tests 65', '2026-01-22 13:18:58', 1);
INSERT INTO `chats` VALUES (304, 1, 2, 'tests 67', '2026-01-22 13:19:08', 1);
INSERT INTO `chats` VALUES (305, 1, 3, 'ggggg', '2026-01-22 13:20:10', 1);
INSERT INTO `chats` VALUES (306, 1, 3, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '2026-01-22 13:22:22', 1);
INSERT INTO `chats` VALUES (307, 1, 3, 'aaa\n\n\n\nllllll', '2026-01-22 13:22:42', 1);
INSERT INTO `chats` VALUES (308, 1, 2, 'tests 68', '2026-01-22 13:24:54', 1);
INSERT INTO `chats` VALUES (309, 1, 2, 'tests 68', '2026-01-22 13:25:10', 1);
INSERT INTO `chats` VALUES (310, 1, 2, 'tests 68', '2026-01-22 13:25:22', 1);
INSERT INTO `chats` VALUES (311, 1, 2, 'tests 68', '2026-01-22 13:26:23', 1);
INSERT INTO `chats` VALUES (312, 1, 2, 'tests 68', '2026-01-22 15:47:21', 1);
INSERT INTO `chats` VALUES (313, 1, 2, 'tests 68', '2026-01-22 15:47:30', 1);
INSERT INTO `chats` VALUES (314, 1, 2, 'tests 68', '2026-01-22 15:47:37', 1);
INSERT INTO `chats` VALUES (315, 1, 2, 'tests 68', '2026-01-22 15:49:59', 1);
INSERT INTO `chats` VALUES (316, 1, 2, 'tests 68', '2026-01-22 15:50:05', 1);
INSERT INTO `chats` VALUES (317, 1, 3, 'tests 68', '2026-01-22 15:50:11', 1);
INSERT INTO `chats` VALUES (318, 1, 3, 'tests 68', '2026-01-22 15:53:58', 1);
INSERT INTO `chats` VALUES (319, 3, 1, 'tests 68', '2026-01-22 16:00:21', 1);
INSERT INTO `chats` VALUES (320, 3, 1, 'tests 68', '2026-01-22 16:04:51', 2);
INSERT INTO `chats` VALUES (321, 2, 1, 'tests 68', '2026-01-22 19:03:40', 2);
INSERT INTO `chats` VALUES (322, 3, 1, 'tests 68', '2026-01-22 19:06:49', 2);
INSERT INTO `chats` VALUES (323, 1, 2, 'tests 68', '2026-01-22 19:13:01', 2);
INSERT INTO `chats` VALUES (324, 1, 2, 'tests 68', '2026-01-22 19:13:11', 2);
INSERT INTO `chats` VALUES (325, 1, 2, 'tests 68', '2026-01-22 19:24:37', 2);
INSERT INTO `chats` VALUES (326, 1, 2, 'tests 68', '2026-01-23 07:22:48', 2);
INSERT INTO `chats` VALUES (327, 1, 2, 'tests 68', '2026-01-23 07:23:02', 2);
INSERT INTO `chats` VALUES (328, 2, 1, 'tests 68', '2026-01-23 07:23:57', 2);
INSERT INTO `chats` VALUES (329, 1, 2, 'tests 64', '2026-01-24 14:07:07', NULL);
INSERT INTO `chats` VALUES (330, 1, 3, 'hi hi hih', '2026-01-24 14:08:40', NULL);
INSERT INTO `chats` VALUES (331, 1, 3, 'hi ☺️', '2026-01-24 14:09:11', NULL);
INSERT INTO `chats` VALUES (332, 1, 2, 'xin chao', '2026-01-24 14:10:44', NULL);
INSERT INTO `chats` VALUES (333, 1, 3, 'chao kha nha', '2026-01-24 15:05:30', NULL);
INSERT INTO `chats` VALUES (334, 1, 2, 'chao khanh nha', '2026-01-24 15:07:17', NULL);
INSERT INTO `chats` VALUES (335, 1, 2, 'ruf', '2026-01-24 15:07:27', NULL);
INSERT INTO `chats` VALUES (336, 1, 2, 'heggk', '2026-01-24 15:07:32', NULL);
INSERT INTO `chats` VALUES (337, 1, 2, 'asd', '2026-01-24 15:09:28', NULL);
INSERT INTO `chats` VALUES (338, 1, 2, 'tests 64', '2026-01-24 15:11:38', NULL);
INSERT INTO `chats` VALUES (339, 1, 2, 'xin chao ban nha minh la Kha', '2026-01-24 15:12:24', NULL);
INSERT INTO `chats` VALUES (340, 1, 2, 'xin chao ban nha minh la Kha', '2026-01-24 15:12:31', NULL);
INSERT INTO `chats` VALUES (341, 1, 2, 'xin chao ban nha minh la Kha', '2026-01-24 15:12:34', NULL);
INSERT INTO `chats` VALUES (342, 1, 2, 'xin chao ban nha minh la Kha', '2026-01-24 15:12:57', NULL);
INSERT INTO `chats` VALUES (343, 1, 3, 'hi ☺️', '2026-01-24 15:14:18', NULL);
INSERT INTO `chats` VALUES (344, 1, 2, 'xin chao ban nha minh la Kha', '2026-01-24 15:43:32', NULL);
INSERT INTO `chats` VALUES (345, 1, 2, 'xin chao ban nha minh la Kha', '2026-01-24 15:43:45', NULL);
INSERT INTO `chats` VALUES (346, 1, 2, 'xin chao ban nha minh la Kha', '2026-01-24 15:46:31', NULL);
INSERT INTO `chats` VALUES (347, 1, 2, 'xin chao ban nha minh la Kha', '2026-01-24 15:53:03', NULL);
INSERT INTO `chats` VALUES (348, 1, 2, 'xin chao ban nha minh la Kha', '2026-01-24 16:39:10', NULL);
INSERT INTO `chats` VALUES (349, 1, 3, 'hi', '2026-01-24 16:42:57', NULL);
INSERT INTO `chats` VALUES (350, 1, 2, 'xin chao ban nha minh la Kha', '2026-01-24 17:13:09', NULL);
INSERT INTO `chats` VALUES (351, 1, 2, 'ban con mon hang nay khong a', '2026-01-24 17:18:47', NULL);
INSERT INTO `chats` VALUES (352, 1, 2, 'ban con mon hang nay khong a', '2026-01-24 17:33:03', NULL);
INSERT INTO `chats` VALUES (353, 1, 2, 'ban con mon hang nay khong a ban oi', '2026-01-24 17:33:20', NULL);
INSERT INTO `chats` VALUES (354, 1, 2, '120 k giam duoc không ạ', '2026-01-24 17:43:19', NULL);
INSERT INTO `chats` VALUES (355, 1, 3, 'hang nay het roi ban oi', '2026-01-24 17:45:50', NULL);
INSERT INTO `chats` VALUES (356, 1, 3, 'alo 12345', '2026-01-24 18:27:45', NULL);

-- ----------------------------
-- Table structure for email_verification
-- ----------------------------
DROP TABLE IF EXISTS `email_verification`;
CREATE TABLE `email_verification`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `otp_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `otp_expiry` datetime NOT NULL,
  `otp_count` int NOT NULL,
  `otp_last_send` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of email_verification
-- ----------------------------

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` int NULL DEFAULT NULL,
  `user_id` int NOT NULL,
  `product_id` int NOT NULL,
  `create_at` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`, `product_id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `favorite_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorite
-- ----------------------------

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `chat_room_id` int NULL DEFAULT NULL,
  `sender_id` int NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `chat_room_id`(`chat_room_id` ASC) USING BTREE,
  INDEX `sender_id`(`sender_id` ASC) USING BTREE,
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NULL DEFAULT NULL,
  `seller_id` int NOT NULL,
  `buyer_id` int NOT NULL,
  `rating_star` int NULL DEFAULT NULL,
  `review_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `create_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `buyer_id`(`buyer_id` ASC) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `seller_id` int NOT NULL,
  `category_id` int NOT NULL,
  `thumbnail_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `price` decimal(12, 2) NOT NULL,
  `status` int NULL DEFAULT 0,
  `create_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `category_id`(`category_id` ASC) USING BTREE,
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `product_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 2, 1, '', 'iphone doi moi', 'new 100%', 2000000.00, 0, '2026-01-14 23:17:29');
INSERT INTO `product` VALUES (2, 3, 2, '', 'samsung j2', 'full box 20 nam', 9999999.00, 0, '2026-01-15 23:18:24');

-- ----------------------------
-- Table structure for product_comment
-- ----------------------------
DROP TABLE IF EXISTS `product_comment`;
CREATE TABLE `product_comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `user_id` int NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `parent_id` int NULL DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `parent_id`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `product_comment_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `product_comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `product_comment_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `product_comment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_comment
-- ----------------------------

-- ----------------------------
-- Table structure for product_image
-- ----------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NULL DEFAULT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `product_image_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_image
-- ----------------------------

-- ----------------------------
-- Table structure for refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `refresh_token`;
CREATE TABLE `refresh_token`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `expiry_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `token`(`token` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `refresh_token_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of refresh_token
-- ----------------------------
INSERT INTO `refresh_token` VALUES (103, 2, 'a2642679-022f-4598-82e7-aceb1310df7f', '2026-01-31 18:29:54');
INSERT INTO `refresh_token` VALUES (105, 3, '2ad0629e-9d8c-46c2-924a-d029ae4fca61', '2026-01-31 19:14:28');

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `sender_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_sender_id_user`(`sender_id` ASC) USING BTREE,
  INDEX `fk_receiver_id_user`(`receiver_id` ASC) USING BTREE,
  CONSTRAINT `fk_receiver_id_user` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sender_id_user` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, 'chat_user_2_user_3', 3, 2);
INSERT INTO `room` VALUES (2, 'chat_user_1_user_3', 1, 3);
INSERT INTO `room` VALUES (3, 'chat_user_1_user_2', 1, 2);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `gender` tinyint(1) NULL DEFAULT 0,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `province_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ward_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `fcm_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'temp User', 'dont_login_this_account', 'temp User ', 'temp@gmail.com', '0123456783', 0, NULL, 'temp', 'temp', '2025-12-29 16:40:12', 'werwerwr');
INSERT INTO `user` VALUES (2, 'kahtarn', '$2a$10$ryYZpjb5q2wNUOu3aQCx3OIxRNqDBsMjhbNsh7.Ct07XAndx8ty4m', 'Tran Minh Kha', '0306231203@caothang.edu.vn', '0334998312', 0, NULL, 'Tỉnh Tây Ninh', 'Phường Thanh Điền', '2026-01-15 21:06:14', 'eBJuOZXBQ8mrUbisGzrdqQ:APA91bGyM2H8LEwmk7eIlgeDla4b4RJ3M6FcxE9_JpK4vd92XMoeKuJWfdephkkctonImbgkf8uItyEG2htMG4zzO7Gbo_VCuyR33In4Gc11yTS9Tmrs67k');
INSERT INTO `user` VALUES (3, 'ndkhanh', '$2a$10$o4USLiceAeDOhXJKnXmu.uAqvey1XxF2WKJ.bmYqp0N4SsDAjZNYq', 'nguyen duy khanh', '0306231207@caothang.edu.vn', '0123456789', 0, NULL, 'Thành phố Hà Nội', 'Phường Ba Đình', '2026-01-15 22:53:14', 'eBJuOZXBQ8mrUbisGzrdqQ:APA91bGyM2H8LEwmk7eIlgeDla4b4RJ3M6FcxE9_JpK4vd92XMoeKuJWfdephkkctonImbgkf8uItyEG2htMG4zzO7Gbo_VCuyR33In4Gc11yTS9Tmrs67k');

SET FOREIGN_KEY_CHECKS = 1;

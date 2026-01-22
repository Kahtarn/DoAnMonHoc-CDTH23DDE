CREATE TABLE email_verification(
  id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  otp_code VARCHAR(6) NOT NULL,
  otp_expiry DATETIME NOT NULL,
  otp_count INT NOT NULL,
  otp_last_send DATETIME NOT NULL
);

CREATE TABLE user(
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(30) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  phone VARCHAR(15) UNIQUE NOT NULL,
  gender BOOLEAN DEFAULT FALSE,
  avatar_url VARCHAR(255),
  province_name INT,
  ward_name INT,
  create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_token (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE categories(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  icon_url VARCHAR(255)
);

CREATE TABLE product(
  id INT PRIMARY KEY AUTO_INCREMENT,
  seller_id INT NOT NULL,
  category_id INT NOT NULL,
  thumbnail_url VARCHAR(255) NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(12, 2) NOT NULL,
  status INT DEFAULT 0, -- 0: Đang bán, 1: Đã bán, 2: Đã ẩn
  create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY(seller_id) REFERENCES user(id) ON DELETE CASCADE,
  FOREIGN KEY(category_id) REFERENCES categories(id)
);

CREATE TABLE product_image(
  id INT PRIMARY KEY AUTO_INCREMENT,
  product_id INT,
  image_url VARCHAR(255),
  FOREIGN KEY(product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE chat_room(
  id INT PRIMARY KEY AUTO_INCREMENT,
  product_id INT UNIQUE,
  buyer_id INT UNIQUE,
  seller_id INT,
  last_message TEXT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY(product_id) REFERENCES product(id) ON DELETE CASCADE,
  FOREIGN KEY(buyer_id) REFERENCES user(id),
  FOREIGN KEY(seller_id) REFERENCES user(id)
);

CREATE TABLE message(
  id INT PRIMARY KEY AUTO_INCREMENT,
  chat_room_id INT,
  sender_id INT,
  content TEXT NOT NULL,
  create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY(chat_room_id) REFERENCES chat_room(id) ON DELETE CASCADE,
  FOREIGN KEY(sender_id) REFERENCES user(id)
);

CREATE TABLE favorite(
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT UNIQUE,
  product_id INT UNIQUE,
  create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE,
  FOREIGN KEY(product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE order(
  id INT PRIMARY KEY AUTO_INCREMENT,
  product_id INT,
  seller_id INT NOT NULL,
  buyer_id INT NOT NULL,
  rating_star INT CHECK(rating_star BETWEEN 1 AND 5), 
  review_text TEXT, 
  create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY(product_id) REFERENCES product(id),
  FOREIGN KEY(seller_id) REFERENCES user(id),
  FOREIGN KEY(buyer_id) REFERENCES user(id)
);

CREATE TABLE product_comment (
  id INT PRIMARY KEY AUTO_INCREMENT,
  product_id INT NOT NULL,
  user_id INT NOT NULL,
  content TEXT NOT NULL,
  parent_id INT DEFAULT NULL, -- reply comment (nếu cần)
  create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
  FOREIGN KEY (parent_id) REFERENCES product_comment(id) ON DELETE CASCADE
);


INSERT INTO categories (name, icon_url) VALUES 
('Điện thoại', 'https://cdn-icons-png.flaticon.com/512/0/191.png'),
('Laptop', 'https://cdn-icons-png.flaticon.com/512/428/428001.png'),
('Phụ kiện', 'https://cdn-icons-png.flaticon.com/512/80/80718.png');

-- kahtarn đăng bán (seller_id = 1)
INSERT INTO product (seller_id, category_id, thumbnail_url, title, description, price, status) VALUES 
(1, 1, 'https://img.tgdd.vn/iphone15.jpg', 'iPhone 15 Pro Max 256GB', 'Máy chính hãng VN/A, còn bảo hành', 28000000.00, 0),
(1, 3, 'https://img.tgdd.vn/mouse.jpg', 'Chuột Logitech G502', 'Chuột gaming ít dùng, như mới', 800000.00, 0);

INSERT INTO product_image (product_id, image_url) VALUES 
(1, 'https://img.tgdd.vn/iphone15_detail1.jpg'),
(1, 'https://img.tgdd.vn/iphone15_detail2.jpg');

-- ndkhanh đăng bán (seller_id = 2)
INSERT INTO product (seller_id, category_id, thumbnail_url, title, description, price, status) VALUES 
(2, 2, 'https://img.tgdd.vn/macbook.jpg', 'MacBook Air M2 8/256', 'Máy nữ dùng kỹ, chỉ trầy nhẹ ở góc', 22000000.00, 0);

-- ndkhanh yêu thích iPhone của kahtarn
INSERT INTO favorite (user_id, product_id) VALUES (2, 1);

-- Tạo phòng chat giữa ndkhanh (buyer) và kahtarn (seller) cho sản phẩm iPhone
INSERT INTO chat_room (product_id, buyer_id, seller_id, last_message) VALUES 
(1, 2, 1, 'Máy này có bớt không bạn?');

-- Tin nhắn trong phòng chat (giả sử chat_room_id = 1)
INSERT INTO message (chat_room_id, sender_id, content) VALUES 
(1, 2, 'Chào bạn, iPhone 15 còn không?'),
(1, 1, 'Chào bạn, máy vẫn còn nhé!'),
(1, 2, 'Máy này có bớt không bạn?');

# --- !Ups

CREATE TABLE `blog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL DEFAULT '',
  `summary` varchar(200) NOT NULL DEFAULT '',
  `content` varchar(2000) NOT NULL DEFAULT '',
  `image` varchar(200) NOT NULL DEFAULT '',
  `view_count` int(11) NOT NULL DEFAULT 0,
  `update_time` timestamp NOT NULL DEFAULT current_timestamp,
  `create_time` timestamp NOT NULL DEFAULT current_timestamp,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


# --- !Downs

drop table blog;
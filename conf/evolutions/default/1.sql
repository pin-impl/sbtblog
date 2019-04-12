
# --- !Ups

CREATE TABLE `blog` (
                      `id` int(11) NOT NULL AUTO_INCREMENT,
                      `title` varchar(100) NOT NULL DEFAULT '',
                      `summary` varchar(200) NOT NULL DEFAULT '',
                      `content` varchar(2000) NOT NULL DEFAULT '',
                      `image` varchar(200) NOT NULL DEFAULT '',
                      `view_count` int(11) NOT NULL DEFAULT '0',
                      `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `blog_category` (
                               `blog` int(10) unsigned NOT NULL,
                               `category` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `blog_tag` (
                          `blog` int(11) NOT NULL,
                          `tag` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `catagory` (
                          `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                          `name` varchar(20) NOT NULL DEFAULT '',
                          `fid` int(11) NOT NULL DEFAULT '0',
                          `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tag` (
                     `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                     `name` varchar(20) NOT NULL DEFAULT '',
                     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `user` (
                      `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                      `username` varchar(50) NOT NULL DEFAULT '',
                      `password` varchar(100) NOT NULL DEFAULT '',
                      `gender` tinyint(3) NOT NULL,
                      `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


# --- !Downs

drop table blog;


drop table blog_category;

drop table blog_tag;

drop table catagory;

drop table tag;

drop table user;
CREATE DATABASE naive_workflow_local;

USE naive_workflow_local;

DROP TABLE IF EXISTS `workflows`;
CREATE TABLE `workflows` (
  `workflow_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `n_steps` INT(11) UNSIGNED NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT NOW(),
  `updated_at` TIMESTAMP NOT NULL DEFAULT NOW() ON UPDATE NOW(),
  `deleted_at` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`workflow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `workflow_executions`;
CREATE TABLE `workflow_executions` (
  `workflow_execution_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `workflow_id` INT(11) UNSIGNED NOT NULL,
  `current_step_index` INT(11) UNSIGNED NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT NOW(),
  `updated_at` TIMESTAMP NOT NULL DEFAULT NOW() ON UPDATE NOW(),
  `deleted_at` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`workflow_execution_id`),
  CONSTRAINT `fk_workflow_id` FOREIGN KEY (`workflow_id`) REFERENCES `workflows` (`workflow_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

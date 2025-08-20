
DELIMITER $$

CREATE TRIGGER trg_bill_items_ai
AFTER INSERT ON bill_items
FOR EACH ROW
BEGIN
  UPDATE items
  SET quantity = quantity - NEW.quantity
  WHERE id = NEW.item_id;

  IF (SELECT quantity FROM items WHERE id = NEW.item_id) < 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient stock';
  END IF;
END$$

CREATE TRIGGER trg_bill_items_ad
AFTER DELETE ON bill_items
FOR EACH ROW
BEGIN
  UPDATE items
  SET quantity = quantity + OLD.quantity
  WHERE id = OLD.item_id;
END$$

CREATE TRIGGER trg_bill_items_au
AFTER UPDATE ON bill_items
FOR EACH ROW
BEGIN
  DECLARE diff INT;
  SET diff = NEW.quantity - OLD.quantity; 
  UPDATE items
  SET quantity = quantity - diff
  WHERE id = NEW.item_id;

  IF (SELECT quantity FROM items WHERE id = NEW.item_id) < 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient stock on update';
  END IF;
END$$

DELIMITER ;
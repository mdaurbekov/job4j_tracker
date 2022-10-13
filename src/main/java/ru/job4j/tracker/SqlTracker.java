package ru.job4j.tracker;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.job4j.tracker.model.Item;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SqlTracker implements Store, AutoCloseable {

    private Connection cn;
    private static final Logger LOG = LogManager.getLogger(SqlTracker.class.getName());

    public void init() {
        try (InputStream in = SqlTracker.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    @Override
    public Item add(Item item) {
        try (PreparedStatement ps = cn.prepareStatement("insert into items (name, created) values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.execute();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOG.info(e.getMessage());
        }
        return item;
    }

    @Override
    public boolean replace(int id, Item item) {
        boolean rezult = false;
        try (PreparedStatement ps = cn.prepareStatement("update items set name = ?, created = ? where id = ?")) {
            ps.setString(1, item.getName());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, id);
            rezult = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.info(e.getMessage());
        }
        return rezult;
    }

    @Override
    public boolean delete(int id) {
        boolean rezult = false;
        try (PreparedStatement ps = cn.prepareStatement("delete from items where id = ?")) {
            ps.setInt(1, id);
            rezult = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.info(e.getMessage());
        }
        return rezult;
    }

    @Override
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement("select * from items")) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    items.add(getItem(resultSet));
                }
            }
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return items;
    }

    @Override
    public List<Item> findByName(String key) {
        List<Item> items = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement("select * from items where name = ?")) {
            ps.setString(1, key);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    items.add(getItem(resultSet));
                }
            }
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return items;
    }

    @Override
    public Item findById(int id) {
        Item rezult = null;
        try (PreparedStatement ps = cn.prepareStatement("select * from items where id = ?")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    rezult = getItem(resultSet);
                }
            }

        } catch (SQLException e) {
            LOG.info(e.getMessage());
        }
        return rezult;
    }

    private Item getItem(ResultSet resultSet) {
        Item item = null;
        try {
            item = new Item(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getTimestamp("created").toLocalDateTime());
        } catch (SQLException e) {
            LOG.info(e.getMessage());
        }
        return item;
    }
}
package manager;

import db.DBConnectionProvider;
import model.*;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private Connection connection = DBConnectionProvider.getINSTANCE().getConnection();
    EventManager eventManager = new EventManager();

    public void add(User user){

        String sql = "Insert into user (user_name,surname,event_id,email) VALUES (?,?,?,?)";
        try {
           PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
           ps.setString(1, user.getUserName());
           ps.setString(2, user.getSurname());
           ps.setInt(3, user.getEvent().getId());
           ps.setString(4, user.getEmail());
           ps.executeUpdate();
           ResultSet resultSet = ps.getGeneratedKeys();
           if (resultSet.next()) {
               int id = resultSet.getInt(1);
              user.setId(id);
           }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAll() {
        String sql = "Select * from user";
        List<User> users = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                users.add(getUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getById(int id) {
        String sql = "Select * from user where id = " + id;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return getUserFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User getUserFromResultSet (ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUserName(resultSet.getString("name"));
        user.setSurname(resultSet.getString("place"));
        int eventId = resultSet.getInt("event_id");
        Event event = eventManager.getById(eventId);
        user.setEmail(resultSet.getString("email"));
        return user;
    }
}

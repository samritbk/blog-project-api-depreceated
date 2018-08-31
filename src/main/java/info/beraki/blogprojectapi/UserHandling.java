package info.beraki.blogprojectapi;

import info.beraki.blogprojectapi.Model.User;

import java.sql.*;

class UserHandling {

        static String addUser(Connection connection, String firebaseId, String fullname, String email){

            int resultSet= 0;
            int lastId=0;
            try {
                Statement statement =connection.createStatement();

                long createdTimestamp=System.currentTimeMillis();

                String sql="INSERT INTO " +
                        "users(firebase_user_id,fullname, email, created_timestamp) " +
                        "VALUES " +
                        "('"+ firebaseId +"','"+ fullname +"','"+ email +"','"+ createdTimestamp +"')";
                resultSet = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                ResultSet rs= statement.getGeneratedKeys();

                if(rs.next())
                    lastId= rs.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return new CustomErrorMessage(1,"Error adding user").toString();
            }

        if(resultSet != 0)
            return new CustomSuccessMessage(0, "Success",lastId).toString();
        else
            return new CustomErrorMessage(1,"ErrorModel adding user").toString();

    }

    static User getUser(Connection connection, String user_id) throws SQLException, NullPointerException {

        User userObject=null;
        Statement statement = connection.createStatement();
        // TODO: Use Preparestatment has better string handling
        String sql = "SELECT * FROM users WHERE firebase_user_id=";
        sql += "'"+user_id+"'";

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.next()) {
            int userId = resultSet.getInt("user_id");
            String firebaseId = resultSet.getString("firebase_user_id");
            String fullName = resultSet.getString("fullname");
            String email = resultSet.getString("email");
            long created_timestamp = resultSet.getLong("created_timestamp");

            userObject=new User();
            userObject.setUserId(userId);
            userObject.setFirebaseId(firebaseId);
            userObject.setFullName(fullName);
            userObject.setEmail(email);
            userObject.setDataCreated(created_timestamp);
        }

        return userObject;
    }
}
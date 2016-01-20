package indexing;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlDAO {
    
    private Connection connection;
    private Statement statement;
    private SqlDAO() {
        /* Chargement du driver JDBC pour MySQL */
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        /* Connexion à la base de données */
        String url = "jdbc:mysql://localhost:3306/Foofle";
        String utilisateur = "foofle";
        String motDePasse = "foo";

        try {
            connection = DriverManager.getConnection(url, utilisateur, motDePasse);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FoofleItem> get(String term) {
        List<FoofleItem> items = new ArrayList<>();
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM LookupTable WHERE term='" + term + "'");
            while (result.next()) {
            	FoofleItem item = new FoofleItem();
                item.setTerm(result.getString("term"));
                item.setLink(result.getString("link"));
                item.setOccur(result.getInt("occur"));
                item.setTfidf(result.getFloat("tfidf"));
                item.setRobertsonTF(result.getFloat("robertsontf"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public int insert(FoofleItem item) {
        int status;
        try {
            status = statement.executeUpdate("INSERT INTO LookupTable (term, occur, link, tfidf, robertsontf) VALUES ('"
                    + item.getTerm() + "', '" + item.getOccur() + "','" + item.getLink() + "','" + item.getTfidf() + "','" + item.getRobertsonTF() + "')");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return status;
    }
    
    public int insert(List<FoofleItem> items) {
        int status;
        int limit = 1000;
        try {
        	System.out.println("Constructing INSERT query of " + items.size() + "..");
        	File a = new File("a");
        	PrintWriter writer = new PrintWriter(a);
        	int cnt = 0;
        	while (cnt < items.size()) {
	        	String queryString = "INSERT INTO LookupTable (term, occur, link, tfidf, robertsontf) VALUES ";
	        	for (int i = 0; i < limit && cnt < items.size(); i++) {
	        		FoofleItem item = items.get(cnt);
	        		queryString += "('" + item.getTerm() + "', '" + item.getOccur() + "','" + item.getLink() + "','" + item.getTfidf() + "','" + item.getRobertsonTF() + "')";
	        		if (i == limit - 1 || cnt == items.size() - 1) {
	        			queryString += ";";
	        		} else {
	        			queryString += ", ";
	        		}
	        		cnt += 1;
	        	}
	        	writer.write(queryString + "\n");
	        	if (cnt == items.size()) {
	        		System.out.println(queryString);
	        	}
	        	//status = statement.executeUpdate(queryString);
        	}
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public void close() {
    	if (connection != null)
            try {
                connection.close();
            } catch (SQLException ignore) {
                ignore.printStackTrace();
            }
    }
    private static SqlDAO sqlDAO;
    public static SqlDAO getInstance() {
        if (sqlDAO == null) {
            sqlDAO = new SqlDAO();
        }
        return sqlDAO;
    }

    public static void main(String[] args) {
        SqlDAO dao = SqlDAO.getInstance();
        System.out.println(dao.get("google"));
        System.out.println(dao.get("nimportequoi"));
        FoofleItem item = new FoofleItem();
        item.setTerm("Firefox");
        item.setLink("www.firefox.com");
        item.setOccur(5);
        System.out.println(dao.insert(item));
    }

}

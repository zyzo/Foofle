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
            ResultSet result = statement.executeQuery("SELECT * FROM LookupTable WHERE term='" + term + "'"
            		+ " COLLATE latin1_general_cs");
            while (result.next()) {
            	FoofleItem item = new FoofleItem();
                item.setTerm(result.getString("term"));
                item.setLink(result.getString("link"));
                item.setOccur(result.getInt("occur"));
                item.setTfidf(result.getFloat("tfidf"));
                item.setRobertsonTF(result.getFloat("robertsontf"));
                item.setNormalizedTF(result.getFloat("normalizedtf"));
                item.setCustomRobertsonTF(result.getFloat("customrobertsontf"));
                item.setHTMLP(result.getFloat("htmlp"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
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
	        	String queryString = "INSERT INTO LookupTable (term, occur, link, tfidf, robertsontf, customrobertsontf, normalizedtf, htmlp) VALUES ";
	        	for (int i = 0; i < limit && cnt < items.size(); i++) {
	        		FoofleItem item = items.get(cnt);
	        		queryString += "('" + item.getTerm() + "', '" + item.getOccur() + "','" + item.getLink() + "','" 
	        		+ item.getTfidf() + "','" 
	        		+ item.getRobertsonTF() + "','"
	        		+ item.getCustomRobertsonTF() + "', '"
	        		+ item.getNormalizedTF() + "','"
	        		+ item.getHtmlp() + "')";
	        		if (i == limit - 1 || cnt == items.size() - 1) {
	        			queryString += ";";
	        		} else {
	        			queryString += ", ";
	        		}
	        		cnt += 1;
	        	}
	        	status = statement.executeUpdate(queryString);
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
    }

}

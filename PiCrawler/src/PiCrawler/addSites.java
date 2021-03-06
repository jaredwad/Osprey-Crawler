/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PiCrawler;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Jared
 */
public class addSites {

   public addSites() {
      sites = new HashSet<String>();
      
      System.out.println("");
      System.out.println("=============================================");
      System.out.println("***** Loading sites into addSites *****");
      System.out.println("=============================================");
      System.out.println("");
      
      try {
      
      //STEP 2: Register JDBC driver
         Class.forName("com.mysql.jdbc.Driver");

         //STEP 3: Open a connection
         System.out.println("Connecting to database...");
         Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

         //STEP 4: Execute a query
         System.out.println("Creating insert statement...");
         Statement stmt = conn.createStatement();
         String sql = "SELECT domain_name from domain";
         ResultSet rs = stmt.executeQuery(sql);
         
         while(rs.next()) {
            sites.add(rs.getString("domain_name"));
         }
      } catch (ClassNotFoundException | SQLException ex) {
         ex.printStackTrace();
      }

   }

   private void addSet(Set<String> pages) {
      
      for(String page : pages) {
        if(!sites.contains(page)) {
           if(!page.contains("redirect"))
              addPage(page);
        }
      }
   }
   
   private void addPage(String page) {
      try {
         
      System.out.println("* Adding page " + page);
      
      
      //STEP 2: Register JDBC driver
         Class.forName("com.mysql.jdbc.Driver");

         //STEP 3: Open a connection
         System.out.println("Connecting to database...");
         Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

         //STEP 4: Execute a query
         System.out.println("Creating insert statement...");
         Statement stmt = conn.createStatement();
         String sql = "INSERT INTO domain(domain_name, crawl, created_on) "
                  + "VALUES ('" + page + "', 1, SYSDATE())";
         stmt.executeUpdate(sql);
         
         
      } catch (ClassNotFoundException | SQLException ex) {
         ex.printStackTrace();
      }
   }

   public void write(Document doc) {
      Document DOM = doc;
      Set<String> newSites = new HashSet<String>();
      String[] parts;
      String linkHref;
      String middle;

      Elements href = DOM.getElementsByAttribute("href");

      for (Element link : href) {
         linkHref = link.attr("href");
         if (linkHref.contains("http")) {
            parts = linkHref.split("/");
            if (parts.length < 3)
               continue;
            middle = "//";
            if (!linkHref.contains("www"))
               middle += "www.";
            linkHref = parts[0] + middle + parts[2]; // only grab the domain name
            newSites.add(linkHref);

         }
      }

      addSet(newSites);

   }
   
   private Set<String> sites;

   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   static final String DB_URL = "jdbc:mysql://localhost/OspreySecurity";

   //  Database credentials
   static final String USER = "OspreySecurity";
   static final String PASS = "osprey";

}

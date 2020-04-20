package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	public List<Rilevamento> getAllRilevamentiiniziali(int mese) {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione WHERE DAY(DATA)<=15 AND MONTH(DATA)=? ORDER BY data,localita ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese,String localita) {
		
		final String sql = "SELECT Localita, Data, Umidita" + 
				" FROM situazione" + 
				" WHERE MONTH(DATA)=? AND Localita=?";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1,mese);
			st.setString(2,localita);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}
			conn.close();
			return rilevamenti;
		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Integer getAvgRilevamentiLocalitaMese(int mese,String localita) {
		int media = 0;
		final String sql = "SELECT AVG(Umidita) as media" + 
				" FROM situazione" + 
				" WHERE MONTH(DATA)=? AND Localita=?";
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1,mese);
			st.setString(2,localita);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				media=rs.getInt("media");
			}
			conn.close();
			return media;
		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public TreeMap<String,Integer> getmediaumiditaMese(int mese) {
		
		final String sql = "SELECT localita,AVG(Umidita) AS umidita_media" + 
				" FROM situazione" + 
				" WHERE MONTH(DATA)=?" + 
				" GROUP BY localita";

		TreeMap<String,Integer> rilevamenti = new TreeMap<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1,mese);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				rilevamenti.put(rs.getString("localita"),rs.getInt("umidita_media"));
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


}

package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;

// Funções de conexão ao banco possuem nomes bem sugestivos, e realizam exatamente
// o que o nome sugere, seguem um mesmo padrão, facilitando o entendimento,
// Sempre instanciando as variavéis para conexão com banco
// em seguida dentro de um try realiza a query com o banco e a manipulação para o 
// retorno dos dados, e por fim fecha a conexão com o bancho
public class ActivityUnitDAO extends Template {
	@Override
	void functions(){
		super.functions();
	}

	// Criação de função para fechar a conexão com o banco, essa função foi criada
	// devido a esse processo se repetir diversas vezes nessa classe
	private void closeConnection(ResultSet rs, Statement stmt, Connection conn){
		if((rs != null) && !rs.isClosed())
		rs.close();
		if((stmt != null) && !stmt.isClosed())
		stmt.close();
		if((conn != null) && !conn.isClosed())
		conn.close();
	}

	public ActivityUnit findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM activityunit WHERE idActivityUnit=?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			closeConnection(rs, stmt, conn);
		}
	}

	@Override
	public void listAll(boolean onlyActive) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();

			rs = stmt.executeQuery("SELECT * FROM activityunit ORDER BY description");

			List<ActivityUnit> list = new ArrayList<ActivityUnit>();

			while(rs.next()){
				list.add(this.loadObject(rs));
			}

			return list;
		}finally{
			closeConnection(rs, stmt, conn);
		}
	}

	@Override
	public void closeConection(Connection conn, PreparedStatement stmt, ResultSet rs) {
		if((rs != null) && !rs.isClosed())
			rs.close();
		if((stmt != null) && !stmt.isClosed())
			stmt.close();
		if((conn != null) && !conn.isClosed())
			conn.close();
	}

	@Override
	public void save(int idUser, Department department) {
		boolean insert = (unit.getIdActivityUnit() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			conn = ConnectionDAO.getInstance().getConnection();

			if(insert){
				stmt = conn.prepareStatement("INSERT INTO activityunit(description, fillAmount, amountDescription) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE activityunit SET description=?, fillAmount=?, amountDescription=? WHERE idActivityUnit=?");
			}

			stmt.setString(1, unit.getDescription());
			stmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
			stmt.setString(3, unit.getAmountDescription());

			if(!insert){
				stmt.setInt(4, unit.getIdActivityUnit());
			}

			stmt.execute();

			if(insert){
				rs = stmt.getGeneratedKeys();

				if(rs.next()){
					unit.setIdActivityUnit(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, unit);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, unit);
			}

			return unit.getIdActivityUnit();
		}finally{
			closeConnection(rs, stmt, conn);
		}
	}

	private ActivityUnit loadObject(ResultSet rs) throws SQLException{
		ActivityUnit unit = new ActivityUnit();
		
		unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
		unit.setDescription(rs.getString("Description"));
		unit.setFillAmount(rs.getInt("fillAmount") == 1);
		unit.setAmountDescription(rs.getString("amountDescription"));
		
		return unit;
	}

}

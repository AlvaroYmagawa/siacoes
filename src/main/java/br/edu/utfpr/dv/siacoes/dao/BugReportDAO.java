package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.model.Module;
import br.edu.utfpr.dv.siacoes.model.User;

// Funções de conexão ao banco possuem nomes bem sugestivos, e realizam exatamente
// o que o nome sugere, seguem um mesmo padrão, facilitando o entendimento,
// Sempre instanciando as variavéis para conexão com banco
// em seguida dentro de um try realiza a query com o banco e a manipulação para o 
// retorno dos dados, e por fim fecha a conexão com o bancho
public class BugReportDAO extends Template {
	@Override
	void functions(){
		super.functions();
	}

	// Criação de função para fechar a conexão com o banco, essa função foi criada
	// devido a esse processo se repetir diversas vezes nessa classe
	public BugReport findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT bugreport.*, \"user\".name " + 
				"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
				"WHERE idBugReport = ?");
		
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

			rs = stmt.executeQuery("SELECT bugreport.*, \"user\".name " +
					"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
					"ORDER BY status, reportdate");
			List<BugReport> list = new ArrayList<BugReport>();

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
		boolean insert = (bug.getIdBugReport() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			conn = ConnectionDAO.getInstance().getConnection();

			if(insert){
				stmt = conn.prepareStatement("INSERT INTO bugreport(idUser, module, title, description, reportDate, type, status, statusDate, statusDescription) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE bugreport SET idUser=?, module=?, title=?, description=?, reportDate=?, type=?, status=?, statusDate=?, statusDescription=? WHERE idBugReport=?");
			}

			stmt.setInt(1, bug.getUser().getIdUser());
			stmt.setInt(2, bug.getModule().getValue());
			stmt.setString(3, bug.getTitle());
			stmt.setString(4, bug.getDescription());
			stmt.setDate(5, new java.sql.Date(bug.getReportDate().getTime()));
			stmt.setInt(6, bug.getType().getValue());
			stmt.setInt(7, bug.getStatus().getValue());
			if(bug.getStatus() == BugStatus.REPORTED){
				stmt.setNull(8, Types.DATE);
			}else{
				stmt.setDate(8, new java.sql.Date(bug.getStatusDate().getTime()));
			}
			stmt.setString(9, bug.getStatusDescription());

			if(!insert){
				stmt.setInt(10, bug.getIdBugReport());
			}

			stmt.execute();

			if(insert){
				rs = stmt.getGeneratedKeys();

				if(rs.next()){
					bug.setIdBugReport(rs.getInt(1));
				}
			}

			return bug.getIdBugReport();
		}finally{
			closeConnection(rs, stmt, conn);
		}
	}



	private BugReport loadObject(ResultSet rs) throws SQLException{
		BugReport bug = new BugReport();
		
		bug.setIdBugReport(rs.getInt("idBugReport"));
		bug.setUser(new User());
		bug.getUser().setIdUser(rs.getInt("idUser"));
		bug.getUser().setName(rs.getString("name"));
		bug.setModule(Module.SystemModule.valueOf(rs.getInt("module")));
		bug.setTitle(rs.getString("title"));
		bug.setDescription(rs.getString("description"));
		bug.setReportDate(rs.getDate("reportDate"));
		bug.setType(BugReport.BugType.valueOf(rs.getInt("type")));
		bug.setStatus(BugReport.BugStatus.valueOf(rs.getInt("status")));
		bug.setStatusDate(rs.getDate("statusDate"));
		bug.setStatusDescription(rs.getString("statusDescription"));
		
		return bug;
	}

}

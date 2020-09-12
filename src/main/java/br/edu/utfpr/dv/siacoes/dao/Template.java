package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class Template {
    public abstratct void functions(){
        closeConection(Connection conn, PreparedStatement stmt, ResultSet rs);
        findById(int id);
        listAll(boolean onlyActive);
        save(int idUser, Department department);
        loadObject(ResultSet rs);
    }

    public abstract void loadObject(ResultSet rs);
    public abstract void  findById(int id);
    public abstract void listAll(boolean onlyActive);
    public abstract void closeConection(Connection conn, PreparedStatement stmt, ResultSet rs);
    public abstract void save(int idUser, Department department);
}

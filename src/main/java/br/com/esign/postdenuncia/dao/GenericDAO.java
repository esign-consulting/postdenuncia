package br.com.esign.postdenuncia.dao;

import java.io.Serializable;

import com.googlecode.genericdao.dao.hibernate.GenericDAOImpl;

public class GenericDAO<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

    public GenericDAO() {
        setSessionFactory(HibernateUtil.getSessionFactory());
    }

}
package br.com.esign.postdenuncia.dao;

import br.com.esign.postdenuncia.model.GooglePlusPerson;
import br.com.esign.postdenuncia.util.MessagesBundle;

import com.googlecode.genericdao.search.Search;

public class GooglePlusPersonDAO extends GenericDAO<GooglePlusPerson, String> {

    public GooglePlusPerson getByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.EMAIL_USUARIO_GOOGLE_PLUS_OBRIGATORIO);
        }
        Search gplusPersonSearch = new Search(GooglePlusPerson.class);
        gplusPersonSearch.addFilterEqual("email", email);
        return searchUnique(gplusPersonSearch);
    }

}
package br.com.esign.postdenuncia.dao;

import br.com.esign.postdenuncia.model.FacebookUser;
import br.com.esign.postdenuncia.util.MessagesBundle;

import com.googlecode.genericdao.search.Search;

public class FacebookUserDAO extends GenericDAO<FacebookUser, String> {

    public FacebookUser getByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.EMAIL_USUARIO_FACEBOOK_OBRIGATORIO);
        }
        Search fbUserSearch = new Search(FacebookUser.class);
        fbUserSearch.addFilterEqual("email", email);
        return searchUnique(fbUserSearch);
    }

}
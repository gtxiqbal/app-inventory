package id.co.gtx.controller;

import id.co.gtx.entity.Login;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Component
@Scope("desktop")
public class LoginController extends SelectorComposer<Component> {

    @Autowired
    private LoginService loginService;
    @Wire
    Textbox txtUsername;
    @Wire
    Textbox txtPassword;
    private Session session;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        session = Executions.getCurrent().getSession();
        if (session.getAttribute("userLogin") != null) {
            Executions.sendRedirect("/mainMenu.zul");
        }
    }

    @Listen("onClick = #btnLogin")
    public void doLogin() {
        if (!validation())
            return;

        DtoResponse response = loginService.doLogin(txtUsername.getValue(), txtPassword.getValue());
        if (response.getStatus().equals("SUCCESS")) {
            Login login = (Login) response.getData();
            session.setAttribute("userLogin", login);
            Executions.sendRedirect("/mainMenu.zul");
        } else {
            Messagebox.show(response.getMsg());
        }
    }

    @Listen("onClick = #btnReset")
    public void doReset() {
        txtUsername.setValue("");
        txtPassword.setValue("");
    }

    private boolean validation() {
        List<WrongValueException> wrongValue = new ArrayList<>();
        if (txtUsername.getValue().equals(""))
            wrongValue.add(new WrongValueException(txtUsername, "Username Tidak Boleh Kosong"));
        if (txtPassword.getValue().equals(""))
            wrongValue.add(new WrongValueException(txtPassword, "Password Tidak Boleh Kosong"));

        if (wrongValue.size() > 0)
            throw new WrongValuesException(wrongValue.toArray(new WrongValueException[wrongValue.size()]));
        return true;
    }
}

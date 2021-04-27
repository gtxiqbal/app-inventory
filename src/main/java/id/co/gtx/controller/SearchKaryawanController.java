package id.co.gtx.controller;

import id.co.gtx.entity.Karyawan;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.services.KaryawanService;
import id.co.gtx.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;
import java.util.Map;

@Slf4j
@org.springframework.stereotype.Component
@Scope("execution")
public class SearchKaryawanController extends SelectorComposer<Component> {
    @Autowired
    private KaryawanService karyawanService;
    @Wire
    Window winSearchKaryawan;
    @Wire
    Longbox txtNik;
    @Wire
    Textbox txtFirstName;
    @Wire
    Textbox txtLastName;
    @Wire
    Textbox txtEmail;
    @Wire
    Groupbox grpListbox;
    @Wire
    Listbox listboxKaryawan;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        doReset();
    }

    @Listen("onChange = #txtNik")
    public void doChangeTxtNik() {
        if (txtNik.getValue() != null) {
            txtFirstName.setDisabled(true);
            txtLastName.setDisabled(true);
            txtEmail.setDisabled(true);
        } else {
            txtFirstName.setDisabled(false);
            txtLastName.setDisabled(false);
            txtEmail.setDisabled(false);
        }
    }

    @Listen("onChange = #txtFirstName")
    public void doChangeTxtFirstName() {
        if (!txtFirstName.getValue().equals("")) {
            txtNik.setDisabled(true);
            txtLastName.setDisabled(true);
            txtEmail.setDisabled(true);
        } else {
            txtNik.setDisabled(false);
            txtLastName.setDisabled(false);
            txtEmail.setDisabled(false);
        }
    }

    @Listen("onChange = #txtLastName")
    public void doChangeTxtLastName() {
        if (!txtLastName.getValue().equals("")) {
            txtNik.setDisabled(true);
            txtFirstName.setDisabled(true);
            txtEmail.setDisabled(true);
        } else {
            txtNik.setDisabled(false);
            txtFirstName.setDisabled(false);
            txtEmail.setDisabled(false);
        }
    }

    @Listen("onChange = #txtEmail")
    public void doChangeTxtEmail() {
        if (!txtEmail.getValue().equals("")) {
            txtNik.setDisabled(true);
            txtFirstName.setDisabled(true);
            txtLastName.setDisabled(true);
        } else {
            txtNik.setDisabled(false);
            txtFirstName.setDisabled(false);
            txtLastName.setDisabled(false);
        }
    }

    @Listen("onClick = #btnFind")
    public void doFind() {
        DtoResponse response = null;
        if (txtNik.getValue() != null)
            response = karyawanService.findByNik(txtNik.getValue());
        else if (!txtFirstName.getValue().equals(""))
            response = karyawanService.findByFirstName(txtFirstName.getValue());
        else if (!txtLastName.getValue().equals(""))
            response = karyawanService.findByLastName(txtLastName.getValue());
        else if (!txtEmail.getValue().equals(""))
            response = karyawanService.findByEmail(txtEmail.getValue());

        if (response == null) {
            Messagebox.show("Kolom Pencarian Harus Diisi.");
            return;
        }

        if (response.getStatus().equals("SUCCESS")) {
            listboxKaryawan.getItems().clear();
            grpListbox.setVisible(true);
            if (response.getData() instanceof List) {
                List<Karyawan> karyawans = (List<Karyawan>) response.getData();
                for (Karyawan karyawan : karyawans) {
                    Map<String, Object> data = MapperUtil.mappingClass(karyawan, Map.class);
                    Listitem item = new Listitem();
                    item.setAttribute("DATA", data);
                    item.appendChild(new Listcell(karyawan.getNik().toString()));

                    String fullName = karyawan.getFirstName();
                    fullName = karyawan.getLastName() != null ? fullName + " " + karyawan.getLastName() : fullName;
                    item.appendChild(new Listcell(fullName));
                    item.appendChild(new Listcell(karyawan.getEmail()));
                    item.appendChild(new Listcell(karyawan.getStatus().getDesc()));
                    item.appendChild(new Listcell(karyawan.getJabatan().getDesc()));
                    listboxKaryawan.appendChild(item);
                }
            } else {
                Karyawan karyawan = (Karyawan) response.getData();
                Map<String, Object> data = MapperUtil.mappingClass(karyawan, Map.class);
                Listitem item = new Listitem();
                item.setAttribute("DATA", data);
                item.appendChild(new Listcell(karyawan.getNik().toString()));

                String fullName = karyawan.getFirstName();
                fullName = karyawan.getLastName() != null ? fullName + " " + karyawan.getLastName() : fullName;
                item.appendChild(new Listcell(fullName));
                item.appendChild(new Listcell(karyawan.getEmail()));
                item.appendChild(new Listcell(karyawan.getStatus().getDesc()));
                item.appendChild(new Listcell(karyawan.getJabatan().getDesc()));
                listboxKaryawan.appendChild(item);
            }
        } else {
            Messagebox.show(response.getMsg());
        }
    }

    @Listen("onClick = #btnReset")
    public void doReset() {
        txtNik.setValue(null);
        txtNik.setDisabled(false);
        txtFirstName.setValue("");
        txtFirstName.setDisabled(false);
        txtLastName.setValue("");
        txtLastName.setDisabled(false);
        txtEmail.setValue("");
        txtEmail.setDisabled(false);
        grpListbox.setVisible(false);
        listboxKaryawan.getItems().clear();
    }

    @Listen("onClick = #btnCancel")
    public void doClose() {
        winSearchKaryawan.onClose();
    }
}

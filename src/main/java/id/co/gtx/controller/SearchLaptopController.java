package id.co.gtx.controller;

import id.co.gtx.entity.Laptop;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.entity.enumz.Merk;
import id.co.gtx.entity.enumz.Type;
import id.co.gtx.services.LaptopService;
import id.co.gtx.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Component
@Scope("execution")
public class SearchLaptopController extends SelectorComposer<Component> {
    @Autowired
    private LaptopService laptopService;
    @Wire
    Textbox txtId;
    @Wire
    Textbox txtName;
    @Wire
    Combobox cmbMerk;
    @Wire
    Combobox cmbType;
    @Wire
    Groupbox grpListbox;
    @Wire
    Listbox listBoxLaptop;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        doReset();
        loadMerk();
    }

    @Listen("onChange = #txtId")
    public void doChangeTxtId() {
        if (!txtId.getValue().equals("")) {
            txtName.setDisabled(true);
            cmbMerk.setDisabled(true);
            cmbType.setDisabled(true);
        } else {
            txtName.setDisabled(false);
            cmbMerk.setDisabled(false);
            cmbType.setDisabled(false);
        }
    }

    @Listen("onChange = #txtName")
    public void doChangeTxtName() {
        if (!txtName.getValue().equals("")) {
            txtId.setDisabled(true);
            cmbMerk.setDisabled(true);
            cmbType.setDisabled(true);
        } else {
            txtId.setDisabled(false);
            cmbMerk.setDisabled(false);
            cmbType.setDisabled(false);
        }
    }

    @Listen("onChange = #cmbMerk")
    public void doChangeCmbMerk() {
        if (cmbMerk.getSelectedIndex() != -1) {
            txtId.setDisabled(true);
            txtName.setDisabled(true);
        } else {
            txtId.setDisabled(false);
            txtName.setDisabled(false);
        }
    }

    @Listen("onClick = #btnFind")
    public void doFind() {
        DtoResponse response = new DtoResponse();

        if (!txtId.getValue().equals("")) {
            response = laptopService.findById(txtId.getValue());
        } else {
            if (!txtName.getValue().equals(""))
                response = laptopService.findByName(txtName.getValue());
            else if (cmbMerk.getSelectedIndex() != -1 && cmbType.getSelectedIndex() == -1)
                response = laptopService.findByMerk(Merk.valueOf(cmbMerk.getSelectedItem().getValue()));
            else if (cmbType.getSelectedIndex() != -1 && cmbType.getSelectedIndex() != -1)
                response = laptopService.findByMerkAndType(Merk.valueOf(cmbMerk.getSelectedItem().getValue()), Type.valueOf(cmbType.getSelectedItem().getValue()));
        }

        if (response.getStatus().equals("SUCCESS")) {
            listBoxLaptop.getItems().clear();
            grpListbox.setVisible(true);
            if (response.getData() instanceof List) {
                List<Laptop> laptops = (List<Laptop>) response.getData();
                if (laptops.size() > 0) {
                    for (Laptop laptop: laptops) {
                        Map<String, Object> data = MapperUtil.mappingClass(laptop, Map.class);
                        Listitem item = new Listitem();
                        item.setAttribute("DATA", data);
                        item.appendChild(new Listcell(laptop.getId()));
                        item.appendChild(new Listcell(laptop.getName()));
                        item.appendChild(new Listcell(laptop.getMerk().getDesc()));
                        item.appendChild(new Listcell(laptop.getType().getValue()));
                        listBoxLaptop.appendChild(item);
                    }
                }
            } else {
                Laptop laptop = (Laptop) response.getData();
                Map<String, Object> data = MapperUtil.mappingClass(laptop, Map.class);
                Listitem item = new Listitem();
                item.setAttribute("DATA", data);
                item.appendChild(new Listcell(laptop.getId()));
                item.appendChild(new Listcell(laptop.getName()));
                item.appendChild(new Listcell(laptop.getMerk().getDesc()));
                item.appendChild(new Listcell(laptop.getType().getValue()));
                listBoxLaptop.appendChild(item);
            }
        } else {
            Messagebox.show(response.getMsg());
        }
    }

    @Listen("onClick = #btnReset")
    public void doReset() {
        txtId.setValue("");
        txtId.setDisabled(false);
        txtName.setValue("");
        txtName.setDisabled(false);
        cmbMerk.setSelectedIndex(-1);
        cmbMerk.setDisabled(false);
        cmbType.setSelectedIndex(-1);
        cmbType.setDisabled(false);
        listBoxLaptop.getItems().clear();
        grpListbox.setVisible(false);
    }

    private void loadMerk() {
        cmbMerk.getItems().clear();
        cmbMerk.setSelectedIndex(-1);
        for (Merk merk: Merk.values()) {
            Comboitem item = new Comboitem();
            item.setValue(merk.getValue());
            item.setLabel(merk.getDesc());
            cmbMerk.appendChild(item);
        }
    }

    @Listen("onChange = #cmbMerk")
    public void doChangeMerk() {
        cmbType.setSelectedIndex(-1);
        cmbType.getItems().clear();
        for (Type type: Type.values()) {
            Comboitem item = new Comboitem();
            if (type.getMerk().getValue().equals(cmbMerk.getSelectedItem().getValue())) {
                item.setLabel(type.getValue() + " - " + type.getMerk().getValue());
                item.setValue(type.getValue());
                cmbType.appendChild(item);
            }
        }
    }
}

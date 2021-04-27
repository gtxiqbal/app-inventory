package id.co.gtx.controller;

import id.co.gtx.entity.Karyawan;
import id.co.gtx.entity.Laptop;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.entity.enumz.Merk;
import id.co.gtx.entity.enumz.Type;
import id.co.gtx.services.LaptopService;
import id.co.gtx.services.SewaService;
import id.co.gtx.util.ComboUtil;
import id.co.gtx.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@org.springframework.stereotype.Component
@Scope("execution")
public class LaptopController extends SelectorComposer<Component> {

    @Autowired
    private LaptopService laptopService;
    @Autowired
    private SewaService sewaService;
    @Wire
    Textbox txtId;
    @Wire
    Textbox txtName;
    @Wire
    Combobox cmbMerk;
    @Wire
    Combobox cmbType;
    @Wire
    Doublebox txtCpu;
    @Wire
    Doublebox txtRam;
    @Wire
    Doublebox txtVga;
    @Wire
    Intbox txtTotal;
    @Wire
    Datebox dtRelease;
    @Wire
    private Listbox listboxLaptop;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        doReset();
        loadMerk();
        loadDataLaptop();
    }

    private void loadDataLaptop() {
        DtoResponse response = laptopService.findAll();
        listboxLaptop.getItems().clear();
        if (response.getStatus().equals("SUCCESS")) {
            List<Laptop> laptops = (List<Laptop>) response.getData();
            for (Laptop laptop: laptops) {
                Map<String, Object> data = MapperUtil.mappingClass(laptop, Map.class);
                Listitem item = new Listitem();
                item.setAttribute("DATA", data);
                item.appendChild(new Listcell(laptop.getId()));
                item.appendChild(new Listcell(laptop.getName()));
                item.appendChild(new Listcell(laptop.getMerk().getValue()));
                item.appendChild(new Listcell(laptop.getType().getValue()));
                item.appendChild(new Listcell(String.valueOf(laptop.getCpu())));
                item.appendChild(new Listcell(String.valueOf(laptop.getRam())));
                item.appendChild(new Listcell(String.valueOf(laptop.getVga())));
                item.appendChild(new Listcell(String.valueOf(laptop.getTotal())));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                item.appendChild(new Listcell(sdf.format(laptop.getRelease())));
                listboxLaptop.appendChild(item);
            }
        }
    }

    @Listen("onDoubleClick = #listboxLaptop")
    public void doEdit() {
        if (listboxLaptop.getSelectedIndex() == -1)
            return;

        Listitem item = listboxLaptop.getSelectedItem();
        Laptop laptop = MapperUtil.mappingClass(item.getAttribute("DATA"), Laptop.class);
        if (laptop != null) {
            txtId.setValue(laptop.getId());
            txtName.setValue(laptop.getName());
            ComboUtil.selectItemById(cmbMerk, laptop.getMerk().getValue());
            doChangeMerk();
            ComboUtil.selectItemById(cmbType, laptop.getType().getValue());
            txtCpu.setValue(laptop.getCpu());
            txtRam.setValue(laptop.getRam());
            txtVga.setValue(laptop.getVga());
            txtTotal.setValue(laptop.getTotal());
            dtRelease.setValue(laptop.getRelease());
        }
    }

    @Listen("onClick = #btnDelete")
    public void doDelete() {
        if (listboxLaptop.getSelectedIndex() == -1)
            return;

        Messagebox.show("Ingin Hapus Data Laptop ?", "KONFIRMASI", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if ((Integer) event.getData() == Messagebox.YES) {
                    Listitem item = listboxLaptop.getSelectedItem();
                    Laptop laptop = MapperUtil.mappingClass(item.getAttribute("DATA"), Laptop.class);
                    DtoResponse response = sewaService.findKaryawanByLaptopId(laptop.getId());
                    List<Karyawan> karyawans = (List<Karyawan>) response.getData();
                    if (response.getStatus().equals("SUCCESS")) {
                        Messagebox.show("Data Laptop Sedang Dipakai, Ingin Paksa Hapus Data Laptop ?", "KONFIRMASI", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                if ((Integer) event.getData() == Messagebox.YES) {
                                    DtoResponse response = sewaService.delete(laptop.getId());
                                    if (response.getStatus().equals("SUCCESS")) {
                                        response = laptopService.delete(laptop.getId());
                                        Messagebox.show(response.getMsg());
                                        if (response.getStatus().equals("SUCCESS")) {
                                            loadDataLaptop();
                                            doReset();
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        response = laptopService.delete(laptop.getId());
                        Messagebox.show(response.getMsg());
                        if (response.getStatus().equals("SUCCESS")) {
                            loadDataLaptop();
                            doReset();
                        }
                    }
                }
            }
        });
    }

    @Listen("onClick = #btnSave")
    public void doSave() {
        if (!validation()) {
            return;
        }

        Laptop laptop = new Laptop();
        laptop.setId(txtId.getValue());
        laptop.setName(txtName.getValue());
        laptop.setMerk(Merk.valueOf(cmbMerk.getSelectedItem().getValue()));
        laptop.setType(Type.valueOf(cmbType.getSelectedItem().getValue()));
        laptop.setCpu(txtCpu.getValue());
        laptop.setRam(txtRam.getValue());
        laptop.setVga(txtVga.getValue());
        laptop.setTotal(txtTotal.getValue());
        laptop.setRelease(dtRelease.getValue());
        DtoResponse response = laptopService.createOrUpdate(laptop);
        Messagebox.show(response.getMsg());
        if (response.getStatus().equals("SUCCESS")) {
            loadDataLaptop();
            doReset();
        }
    }

    @Listen("onClick = #btnReset")
    public void doReset() {
        txtId.setValue(laptopService.generateId());
        txtName.setValue("");
        cmbMerk.setSelectedIndex(-1);
        cmbType.setSelectedIndex(-1);
        cmbType.getItems().clear();
        txtCpu.setValue(null);
        txtRam.setValue(null);
        txtVga.setValue(null);
        txtTotal.setValue(null);
        dtRelease.setValue(new Date());
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

    private boolean validation() {
        boolean isValid = true;
        List<WrongValueException> wrongValue = new ArrayList<>(0);

        if (txtId.getValue().equals(""))
            wrongValue.add(new WrongValueException(txtId, "ID Harus Diisi."));
        if (txtName.getValue().equals(""))
            wrongValue.add(new WrongValueException(txtName, "Nama Harus Diisi."));
        if (cmbMerk.getSelectedIndex() == -1)
            wrongValue.add(new WrongValueException(cmbMerk, "Merk Harus Dipilih."));
        if (cmbType.getSelectedIndex() == -1)
            wrongValue.add(new WrongValueException(cmbType, "Type Harus Dipilih."));
        if (txtCpu.getValue() == null)
            wrongValue.add(new WrongValueException(txtCpu, "CPU Harus Diisi."));
        else if (txtCpu.getValue() == 0.00)
            wrongValue.add(new WrongValueException(txtCpu, "CPU Tidak Boleh Diisi 0."));
        if (txtRam.getValue() == null)
            wrongValue.add(new WrongValueException(txtRam, "RAM Harus Diisi."));
        else if (txtRam.getValue() == 0.00)
            wrongValue.add(new WrongValueException(txtRam, "RAM Tidak Boleh Diisi 0."));
        if (txtVga.getValue() == null)
            wrongValue.add(new WrongValueException(txtVga, "VGA Harus Diisi."));
        else if (txtVga.getValue() == 0.00)
            wrongValue.add(new WrongValueException(txtVga, "VGA Tidak Boleh Diisi 0."));
        if (txtTotal.getValue() == null)
            wrongValue.add(new WrongValueException(txtTotal, "Total Harus Diisi."));
        if (dtRelease.getValue() == null)
            wrongValue.add(new WrongValueException(dtRelease, "Tanggal Rilis Harus Diisi."));

        if (wrongValue.size() > 0) {
            throw new WrongValuesException(wrongValue.toArray(new WrongValueException[wrongValue.size()]));
        }

        return isValid;
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
}

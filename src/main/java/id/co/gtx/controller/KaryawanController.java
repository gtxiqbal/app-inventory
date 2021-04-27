package id.co.gtx.controller;

import id.co.gtx.entity.Karyawan;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.entity.enumz.Dept;
import id.co.gtx.entity.enumz.Divisi;
import id.co.gtx.entity.enumz.Jabatan;
import id.co.gtx.entity.enumz.Status;
import id.co.gtx.services.KaryawanService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@org.springframework.stereotype.Component
@Scope("execution")
public class KaryawanController extends SelectorComposer<Component> {

    @Autowired
    private KaryawanService karyawanService;
    @Autowired
    private SewaService sewaService;
    @Wire
    Window win;
    @Wire
    Longbox txtNik;
    @Wire
    Longbox txtNikOld;
    @Wire
    Textbox txtFirstName;
    @Wire
    Textbox txtLastName;
    @Wire
    Textbox txtEmail;
    @Wire
    Textbox txtNoHp;
    @Wire
    Combobox cmbDivisi;
    @Wire
    Combobox cmbDept;
    @Wire
    Combobox cmbJabatan;
    @Wire
    Combobox cmbStatus;
    @Wire
    Textbox txtTmpLahir;
    @Wire
    Datebox dtTglLahir;
    @Wire
    Datebox dtTglMasuk;
    @Wire
    Listbox listboxKaryawan;
    private boolean onLoad = false;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        doReset();
    }

    private void loadDataKaryawan() {
        listboxKaryawan.getItems().clear();
        DtoResponse response = karyawanService.findAll();
        if (response.getStatus().equals("SUCCESS")) {
            List<Karyawan> karyawans = (List<Karyawan>) response.getData();
            if (karyawans.size() > 0) {
                for (Karyawan karyawan: karyawans) {
                    Listitem item = new Listitem();
                    Map<String, Object> data = MapperUtil.mappingClass(karyawan, Map.class);
                    item.setAttribute("DATA", data);
                    item.appendChild(new Listcell(karyawan.getNik().toString()));

                    String fullName = karyawan.getFirstName();
                    fullName = karyawan.getLastName() != null ? fullName + " " + karyawan.getLastName() : fullName;
                    item.appendChild(new Listcell(fullName));
                    item.appendChild(new Listcell(karyawan.getEmail()));
                    item.appendChild(new Listcell(karyawan.getNoHp()));
                    item.appendChild(new Listcell(karyawan.getDivisi().getDesc()));
                    item.appendChild(new Listcell(karyawan.getDept().getDesc()));
                    item.appendChild(new Listcell(karyawan.getJabatan().getDesc()));
                    item.appendChild(new Listcell(karyawan.getStatus().getDesc()));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    item.appendChild(new Listcell(karyawan.getTmpLahir() + ", " + sdf.format(karyawan.getTglLahir())));
                    item.appendChild(new Listcell(sdf.format(karyawan.getTglMasuk())));
                    listboxKaryawan.appendChild(item);
                }
            }
        }
    }

    @Listen("onDoubleClick = #listboxKaryawan")
    public void doEdit() {
        if (listboxKaryawan.getSelectedIndex() == -1)
            return;

        Listitem item = listboxKaryawan.getSelectedItem();
        Karyawan karyawan = MapperUtil.mappingClass(item.getAttribute("DATA"), Karyawan.class);
        ComboUtil.selectItemById(cmbStatus, String.valueOf(karyawan.getStatus().getValue()));
        doChangeStatus();
        txtNik.setValue(karyawan.getNik());
        txtNikOld.setValue(karyawan.getNik());
        txtFirstName.setValue(karyawan.getFirstName());
        txtLastName.setValue(karyawan.getLastName());
        txtEmail.setValue(karyawan.getEmail());
        txtNoHp.setValue(karyawan.getNoHp());
        ComboUtil.selectItemById(cmbDivisi, karyawan.getDivisi().getValue());
        doChangeDept();
        ComboUtil.selectItemById(cmbDept, karyawan.getDept().getValue());
        ComboUtil.selectItemById(cmbJabatan, karyawan.getJabatan().getValue());
        txtTmpLahir.setValue(karyawan.getTmpLahir());
        dtTglLahir.setValue(karyawan.getTglLahir());
        dtTglMasuk.setValue(karyawan.getTglMasuk());
        onLoad = true;
    }

    @Listen("onChange = #cmbStatus")
    public void doChangeStatus() {
        DtoResponse karyawanResp = karyawanService.findByNik(txtNik.getValue());
        Karyawan karyawan = null;
        if (karyawanResp.getStatus().equals("SUCCESS"))
            karyawan = (Karyawan) karyawanResp.getData();

        Integer status = cmbStatus.getSelectedItem().getValue();
        if (karyawan == null) {
            if (txtNik.getValue() != txtNikOld.getValue() && onLoad) {
                txtNik.setValue(txtNikOld.getValue());
            } else {
                if (status != 0)
                    txtNik.setValue(karyawanService.generateNik());
                else
                    txtNik.setValue(karyawanService.generateNikOutsouring());
            }
        } else {
            if (status != 0 && karyawan.getStatus().getValue() == 0)
                txtNik.setValue(karyawanService.generateNik());
            else if (status != 0)
                txtNik.setValue(karyawan.getNik());
            else
                txtNik.setValue(karyawanService.generateNikOutsouring());
        }

        cmbJabatan.setSelectedIndex(-1);
        cmbJabatan.getItems().clear();
        for (Jabatan jabatan: Jabatan.values()) {
            String kadiv = Jabatan.KADIV.getValue();
            String kadept = Jabatan.KADEPT.getValue();
            String asman = Jabatan.ASMAN.getValue();
            if ((status == 0 || status == 2) && (!kadiv.equals(jabatan.getValue()) && !kadept.equals(jabatan.getValue())) && !asman.equals(jabatan.getValue())) {
                Comboitem item = new Comboitem();
                item.setLabel(jabatan.getValue() + " - " + jabatan.getDesc());
                item.setValue(jabatan.getValue());
                cmbJabatan.appendChild(item);
            } else if (status == 1){
                Comboitem item = new Comboitem();
                item.setLabel(jabatan.getValue() + " - " + jabatan.getDesc());
                item.setValue(jabatan.getValue());
                cmbJabatan.appendChild(item);
            }
        }
    }

    @Listen("onChange = #cmbDivisi")
    public void doChangeDept() {
        cmbDept.setSelectedIndex(-1);
        cmbDept.getItems().clear();
        for (Dept dept: Dept.values()) {
            if (dept.getDivis().getValue().equals(cmbDivisi.getSelectedItem().getValue())) {
                Comboitem item = new Comboitem();
                item.setLabel(dept.getValue() + " - " + dept.getDesc());
                item.setValue(dept.getValue());
                cmbDept.appendChild(item);
            }
        }
    }

    @Listen("onClick = #btnSave")
    public void doSave() {
        if (!validation())
            return;
        Karyawan karyawan = new Karyawan();
        karyawan.setNik(txtNik.getValue());
        karyawan.setFirstName(txtFirstName.getValue());
        karyawan.setLastName(txtLastName.getValue());
        karyawan.setEmail(txtEmail.getValue());
        karyawan.setNoHp(txtNoHp.getValue());
        karyawan.setDivisi(Divisi.valueOf(cmbDivisi.getSelectedItem().getValue()));
        karyawan.setDept(Dept.valueOf(cmbDept.getSelectedItem().getValue()));
        karyawan.setJabatan(Jabatan.valueOf(cmbJabatan.getSelectedItem().getValue()));
        karyawan.setStatus(getStatus(cmbStatus.getSelectedItem().getValue()));
        karyawan.setTmpLahir(txtTmpLahir.getValue());
        karyawan.setTglLahir(dtTglLahir.getValue());
        karyawan.setTglMasuk(dtTglMasuk.getValue());
        DtoResponse response = karyawanService.createOrUpdate(karyawan, onLoad, txtNikOld.getValue());
        Messagebox.show(response.getMsg());
        if (response.getStatus().equals("SUCCESS"))
            doReset();
    }

    private Status getStatus(Integer status) {
        if (status == 0)
            return Status.OUTSOURCING;
        else if (status == 1)
            return Status.TETAP;
        else if (status == 2)
            return Status.KONTRAK;
        else
            throw new WrongValueException("Kode Status Tersedia: " + Status.values().toString());
    }

    @Listen("onClick = #btnReset")
    public void doReset() {
        txtNik.setValue(karyawanService.generateNik());
        txtNikOld.setValue(karyawanService.generateNik());
        txtFirstName.setValue(null);
        txtLastName.setValue(null);
        txtEmail.setValue(null);
        txtNoHp.setValue(null);
        cmbDivisi.setSelectedIndex(-1);
        cmbDept.setSelectedIndex(-1);
        cmbDept.getItems().clear();
        cmbJabatan.setSelectedIndex(-1);
        cmbStatus.setSelectedIndex(-1);
        txtTmpLahir.setValue(null);
        dtTglLahir.setValue(null);
        dtTglMasuk.setValue(new Date());

        loadDataKaryawan();
        loadDataKaryawan();
        loadDivis();
        loadStatus();
        onLoad = false;
    }

    @Listen("onClick = #btnDelete")
    public void doDelete() {
        if (listboxKaryawan.getSelectedIndex() == -1)
            return;

        Messagebox.show("Ingin Hapus Data Karyawan ?", "KONFIRMASI", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if ((Integer) event.getData() == Messagebox.YES) {
                    Listitem item = listboxKaryawan.getSelectedItem();
                    Karyawan karyawan = MapperUtil.mappingClass(item.getAttribute("DATA"), Karyawan.class);
                    DtoResponse response = sewaService.findLaptopByKaryawanNik(karyawan.getNik());
                    if (response.getStatus().equals("SUCCESS")) {
                        Messagebox.show("Data Karyawan Sudah Terbinding, Ingin Paksa Hapus Data Karyawan ?", "KONFIRMASI", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                if ((Integer) event.getData() == Messagebox.YES) {
                                    DtoResponse response = sewaService.delete(karyawan.getNik());
                                    if (response.getStatus().equals("SUCCESS")) {
                                        response = karyawanService.delete(karyawan.getNik());
                                        Messagebox.show(response.getMsg());
                                        if (response.getStatus().equals("SUCCESS"))
                                            doReset();
                                    }
                                }
                            }
                        });
                    } else {
                        response = karyawanService.delete(karyawan.getNik());
                        Messagebox.show(response.getMsg());
                        if (response.getStatus().equals("SUCCESS"))
                            doReset();
                    }
                }
            }
        });
    }

    private void loadDivis() {
        cmbDivisi.setSelectedIndex(-1);
        cmbDivisi.getItems().clear();
        for (Divisi divisi: Divisi.values()) {
            Comboitem item = new Comboitem();
            item.setValue(divisi.getValue());
            item.setLabel(divisi.getValue() + " - " + divisi.getDesc());
            cmbDivisi.appendChild(item);
        }
    }

    private void loadStatus() {
        cmbStatus.setSelectedIndex(-1);
        cmbStatus.getItems().clear();
        for (Status status: Status.values()) {
            Comboitem item = new Comboitem();
            item.setValue(status.getValue());
            item.setLabel(status.getValue() + " - " + status.getDesc());
            cmbStatus.appendChild(item);
        }
    }

    private boolean validation() {
        boolean isValid = true;
        List<WrongValueException> wrongValue = new ArrayList<>();

        if (txtNik.getValue().equals(""))
            wrongValue.add(new WrongValueException(txtNik, "NIK Harus Diisi"));
        if (txtFirstName.getValue().equals(""))
            wrongValue.add(new WrongValueException(txtFirstName, "Nama Depan Harus Diisi"));
        if (txtLastName.getValue().equals(""))
            wrongValue.add(new WrongValueException(txtLastName, "Nama Belakang Harus Diisi"));
        if (txtEmail.getValue().equals(""))
            wrongValue.add(new WrongValueException(txtEmail, "Email Harus Diisi"));
        if (txtNoHp.getValue().equals(""))
            wrongValue.add(new WrongValueException(txtNoHp, "No. HP Harus Diisi"));
        if (cmbDivisi.getSelectedIndex() == -1)
            wrongValue.add(new WrongValueException(cmbDivisi, "Divisi Harus Dipilih"));
        if (cmbDept.getSelectedIndex() == -1)
            wrongValue.add(new WrongValueException(cmbDept, "Departement Harus Dipilih"));
        if (cmbJabatan.getSelectedIndex() == -1)
            wrongValue.add(new WrongValueException(cmbJabatan, "Jabatan Harus Dipilih"));
        if (cmbStatus.getSelectedIndex() == -1)
            wrongValue.add(new WrongValueException(cmbStatus, "Status Pekerja Harus Dipilih"));
        if (txtTmpLahir.getValue().equals(""))
            wrongValue.add(new WrongValueException(txtTmpLahir, "Tempat Lahir Harus Diisi"));
        if (dtTglLahir.getValue() == null)
            wrongValue.add(new WrongValueException(dtTglLahir, "Tanggal Lahir Harus Diisi"));
        if (dtTglMasuk.getValue() == null)
            wrongValue.add(new WrongValueException(dtTglMasuk, "Tanggal Masuk Kerja Harus Diisi"));

        if (wrongValue.size() > 0)
            throw new WrongValuesException(wrongValue.toArray(new WrongValueException[wrongValue.size()]));

        return isValid;
    }
}

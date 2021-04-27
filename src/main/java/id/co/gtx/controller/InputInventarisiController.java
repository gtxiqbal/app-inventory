package id.co.gtx.controller;

import id.co.gtx.entity.Karyawan;
import id.co.gtx.entity.Laptop;
import id.co.gtx.entity.Sewa;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.services.KaryawanService;
import id.co.gtx.services.SewaService;
import id.co.gtx.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

@Slf4j
@org.springframework.stereotype.Component
@Scope("execution")
public class InputInventarisiController extends SelectorComposer<Component> {

    @Autowired
    private KaryawanService karyawanService;
    @Autowired
    private SewaService sewaService;
    @Wire
    Window winInputInventaris;
    @Wire
    Button btnFindKaryawan;
    @Wire
    Longbox txtNik;
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
    Listbox listboxSewaLaptop;
    private Karyawan karyawan;
    private Long nik;
    private List<Long> niks = new ArrayList<>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Execution current = Executions.getCurrent();
        Map<String, Object> arg = (Map<String, Object>) current.getArg();
        if (!arg.isEmpty()) {
            if (arg.get("NIKS") != null) {
                niks = (List<Long>) arg.get("NIKS");
            } else if (arg.get("NIK") != null) {
                nik = (Long) arg.get("NIK");
                loadInventaris(nik);
            }
        }
    }

    private void loadInventaris(Long nik) {
        DtoResponse response = karyawanService.findByNik(nik);
        if (response.getStatus().equals("SUCCESS")) {
            btnFindKaryawan.setDisabled(true);
            karyawan = (Karyawan) response.getData();
            txtNik.setValue(karyawan.getNik());
            txtFirstName.setValue(karyawan.getFirstName());
            txtLastName.setValue(karyawan.getLastName());
            txtEmail.setValue(karyawan.getEmail());
            txtNoHp.setValue(karyawan.getNoHp());

            Comboitem item = new Comboitem();
            item.setValue(karyawan.getStatus().getValue());
            item.setLabel(karyawan.getStatus().getDesc());
            cmbStatus.appendChild(item);
            cmbStatus.setSelectedIndex(0);

            item = new Comboitem();
            item.setValue(karyawan.getDivisi().getValue());
            item.setLabel(karyawan.getDivisi().getDesc());
            cmbDivisi.appendChild(item);
            cmbDivisi.setSelectedIndex(0);

            item = new Comboitem();
            item.setValue(karyawan.getDept().getValue());
            item.setLabel(karyawan.getDept().getDesc());
            cmbDept.appendChild(item);
            cmbDept.setSelectedIndex(0);

            item = new Comboitem();
            item.setValue(karyawan.getJabatan().getValue());
            item.setLabel(karyawan.getJabatan().getDesc());
            cmbJabatan.appendChild(item);
            cmbJabatan.setSelectedIndex(0);

            txtTmpLahir.setValue(karyawan.getTmpLahir());
            dtTglLahir.setValue(karyawan.getTglLahir());
            dtTglMasuk.setValue(karyawan.getTglMasuk());

            response = sewaService.findByNik(nik);
            if (response.getStatus().equals("SUCCESS")) {
                List<Sewa> sewas = (List<Sewa>) response.getData();
                listboxSewaLaptop.getItems().clear();
                for (Sewa sewa: sewas) {
                    Laptop laptop = sewa.getLaptop();
                    Map<String, Object> dataSewa = MapperUtil.mappingClass(sewa, Map.class);
                    Listitem listitem = new Listitem();
                    listitem.setAttribute("DATA", dataSewa);

                    listitem.appendChild(new Listcell(laptop.getId()));
                    listitem.appendChild(new Listcell(laptop.getName()));
                    listitem.appendChild(new Listcell(laptop.getMerk().getDesc()));
                    listitem.appendChild(new Listcell(laptop.getType().getValue()));

                    DecimalFormat df = new DecimalFormat("#,###0.0");
                    listitem.appendChild(new Listcell(df.format(laptop.getCpu())));
                    listitem.appendChild(new Listcell(df.format(laptop.getRam())));
                    listitem.appendChild(new Listcell(df.format(laptop.getVga())));

                    Intbox txtTotal = new Intbox();
                    txtTotal.setId("txtStok-" + listboxSewaLaptop.getItems().size());
                    txtTotal.setValue(sewa.getTotal());
                    txtTotal.setHflex("1");
                    Listcell cell = new Listcell();
                    cell.appendChild(txtTotal);
                    listitem.appendChild(cell);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    listitem.appendChild(new Listcell(sdf.format(sewa.getTglSewa())));

                    Datebox dtTglPengembalian = new Datebox();
                    dtTglPengembalian.setId("dtTglPengembalian-" + listboxSewaLaptop.getItems().size());
                    dtTglPengembalian.setValue(sewa.getTglPengembalian());
                    dtTglPengembalian.setHflex("1");
                    dtTglPengembalian.setFormat("yyyy-MM-dd");
                    cell = new Listcell();
                    cell.appendChild(dtTglPengembalian);
                    listitem.appendChild(cell);

                    listboxSewaLaptop.appendChild(listitem);
                    doAction(txtTotal, dtTglPengembalian, sewa);
                }
            }
        } else {
            Messagebox.show(response.getMsg());
        }
    }

    public void doAction(Intbox txtTotal, Datebox dtTglPengembalian, Sewa sewa) {
        txtTotal.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Listbox listbox = (Listbox) winInputInventaris.getFellow("listboxSewaLaptop");
                for (Listitem item1: listbox.getItems()) {
                    Map<String, Object> data1 = MapperUtil.mappingClass(item1.getAttribute("DATA"), Map.class);
                    Karyawan karyawan1 = MapperUtil.mappingClass(data1.get("karyawan"), Karyawan.class);
                    Laptop laptop1 = MapperUtil.mappingClass(data1.get("laptop"), Laptop.class);
                    if (sewa.getKaryawan().getNik().equals(karyawan1.getNik()) && sewa.getLaptop().getId().equals(laptop1.getId()))
                        data1.put("total", txtTotal.getValue());
                }
            }
        });

        dtTglPengembalian.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Listbox listbox = (Listbox) winInputInventaris.getFellow("listboxSewaLaptop");
                for (Listitem item1: listbox.getItems()) {
                    Map<String, Object> data1 = MapperUtil.mappingClass(item1.getAttribute("DATA"), Map.class);
                    Karyawan karyawan1 = MapperUtil.mappingClass(data1.get("karyawan"), Karyawan.class);
                    Laptop laptop1 = MapperUtil.mappingClass(data1.get("laptop"), Laptop.class);
                    if (sewa.getKaryawan().getNik().equals(karyawan1.getNik()) && sewa.getLaptop().getId().equals(laptop1.getId()))
                        data1.put("tglPengembalian", dtTglPengembalian.getValue());
                }
            }
        });
    }

    @Listen("onClick = #btnFindKaryawan")
    public void doFindKaryawan() {
        Window window = (Window) Executions.createComponents("/view/searchKaryawan.zul", null, null);
        Listbox listboxKaryawan = (Listbox) window.getFellow("listboxKaryawan");
        Button btnCancel = (Button) window.getFellow("btnCancel");
        listboxKaryawan.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (listboxKaryawan.getSelectedIndex() == -1)
                    return;

                Listitem listitem = listboxKaryawan.getSelectedItem();
                if (listitem.getAttribute("DATA") != null) {
                    karyawan = MapperUtil.mappingClass(listitem.getAttribute("DATA"), Karyawan.class);
                    for (Long nik: niks) {
                        if (nik.equals(karyawan.getNik())) {
                            Messagebox.show("Karyawan Dengan NIK: " + nik + " Sudah Terdaftar.");
                            return;
                        }
                    }
                    txtNik.setValue(karyawan.getNik());
                    txtFirstName.setValue(karyawan.getFirstName());
                    txtLastName.setValue(karyawan.getLastName());
                    txtEmail.setValue(karyawan.getEmail());
                    txtNoHp.setValue(karyawan.getNoHp());
                    txtNoHp.setValue(karyawan.getNoHp());

                    Comboitem item = new Comboitem();
                    item.setValue(karyawan.getStatus().getValue());
                    item.setLabel(karyawan.getStatus().getDesc());
                    cmbStatus.appendChild(item);
                    cmbStatus.setSelectedIndex(0);

                    item = new Comboitem();
                    item.setValue(karyawan.getDivisi().getValue());
                    item.setLabel(karyawan.getDivisi().getDesc());
                    cmbDivisi.appendChild(item);
                    cmbDivisi.setSelectedIndex(0);

                    item = new Comboitem();
                    item.setValue(karyawan.getDept().getValue());
                    item.setLabel(karyawan.getDept().getDesc());
                    cmbDept.appendChild(item);
                    cmbDept.setSelectedIndex(0);

                    item = new Comboitem();
                    item.setValue(karyawan.getJabatan().getValue());
                    item.setLabel(karyawan.getJabatan().getDesc());
                    cmbJabatan.appendChild(item);
                    cmbJabatan.setSelectedIndex(0);

                    txtTmpLahir.setValue(karyawan.getTmpLahir());
                    dtTglLahir.setValue(karyawan.getTglLahir());
                    dtTglMasuk.setValue(karyawan.getTglMasuk());
                    window.detach();
                }
            }
        });
        btnCancel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                window.detach();
            }
        });
        window.doModal();
    }

    @Listen("onClick = #btnAddLaptop")
    public void doAddLaptop() {
        if (karyawan == null) {
            Messagebox.show("Data Karyawan Harus Diisi. ");
            return;
        }
        Window window = (Window) Executions.createComponents("/view/searchLaptop.zul", null, null);
        Listbox listBoxLaptop = (Listbox) window.getFellow("listBoxLaptop");
        Button btnCancel = (Button) window.getFellow("btnCancel");
        listBoxLaptop.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (listBoxLaptop.getSelectedIndex() == -1)
                    return;

                Listitem listitem = listBoxLaptop.getSelectedItem();
                if (listitem.getAttribute("DATA") != null) {
                    Map<String, Object> dataLaptop = (Map<String, Object>) listitem.getAttribute("DATA");
                    Laptop laptop = MapperUtil.mappingClass(dataLaptop, Laptop.class);

                    Map<String, Object> data = new HashMap<>();
                    data.put("laptop", laptop);
                    data.put("karyawan", karyawan);
                    data.put("total", 1);
                    data.put("tglSewa", new Date(new Date().getTime()));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    data.put("tglPengembalian", calendar.getTime());

                    Sewa sewa = MapperUtil.mappingClass(data, Sewa.class);

                    for (Listitem listitem1: listboxSewaLaptop.getItems()) {
                        Laptop laptop1 = MapperUtil.mappingClass(((Map<String, Object>) listitem1.getAttribute("DATA")).get("laptop"), Laptop.class);
                        if (laptop1.getId().equals(laptop.getId())) {
                            Messagebox.show("Data Laptop Sudah Ada. ");
                            return;
                        }
                    }

                    Listitem item = new Listitem();
                    item.setAttribute("DATA", data);
                    item.appendChild(new Listcell(laptop.getId()));
                    item.appendChild(new Listcell(laptop.getName()));
                    item.appendChild(new Listcell(laptop.getMerk().getDesc()));
                    item.appendChild(new Listcell(laptop.getType().getValue()));

                    DecimalFormat df = new DecimalFormat("#,###0.0");
                    item.appendChild(new Listcell(df.format(laptop.getCpu())));
                    item.appendChild(new Listcell(df.format(laptop.getRam())));
                    item.appendChild(new Listcell(df.format(laptop.getVga())));

                    Intbox txtTotal = new Intbox();
                    txtTotal.setId("txtStok-" + listboxSewaLaptop.getItems().size());
                    txtTotal.setValue(1);
                    txtTotal.setHflex("1");
                    Listcell cell = new Listcell();
                    cell.appendChild(txtTotal);
                    item.appendChild(cell);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    item.appendChild(new Listcell(sdf.format(new Date())));

                    Datebox dtTglPengembalian = new Datebox();
                    dtTglPengembalian.setId("dtTglPengembalian-" + listboxSewaLaptop.getItems().size());
                    dtTglPengembalian.setValue(calendar.getTime());
                    dtTglPengembalian.setHflex("1");
                    dtTglPengembalian.setFormat("yyyy-MM-dd");
                    cell = new Listcell();
                    cell.appendChild(dtTglPengembalian);
                    item.appendChild(cell);

                    listboxSewaLaptop.appendChild(item);
                    doAction(txtTotal, dtTglPengembalian, sewa);
                    window.detach();
                }
            }
        });
        btnCancel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                window.detach();
            }
        });
        window.doModal();
    }

    @Listen("onClick = #btnDeleteLaptop")
    public void doDeleteLaptop() {
        if (listboxSewaLaptop.getSelectedIndex() == -1)
            return;

        Messagebox.show("Ingin Hapus Data Laptop ?", "KONFIRMASI", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if ((Integer) event.getData() == Messagebox.YES) {
                    Listitem item = listboxSewaLaptop.getSelectedItem();
                    if (item.getAttribute("DATA") != null) {
                        item.removeAttribute("DATA");
                        item.detach();
                    }
                }
            }
        });
    }

    @Listen("onDoubleClick = #listboxSewaLaptop")
    public void doSelectedInventaris() {
        if (listboxSewaLaptop.getSelectedIndex() == -1)
            return;

        Listitem item = listboxSewaLaptop.getSelectedItem();
        Map<String, Object> data = (Map<String, Object>) item.getAttribute("DATA");
        Laptop laptop = MapperUtil.mappingClass(data.get("laptop"), Laptop.class);
        Karyawan karyawan = MapperUtil.mappingClass(data.get("karyawan"), Karyawan.class);
        log.info("AYA INPUTAN TOTAL: " + laptop);
        log.info("AYA NIK: " + karyawan.getNik());
    }
}

package id.co.gtx.controller;

import id.co.gtx.entity.Sewa;
import id.co.gtx.entity.dto.DtoKaryawanTotalLaptop;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.services.SewaService;
import id.co.gtx.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@org.springframework.stereotype.Component
@Scope("execution")
public class InventarisController extends SelectorComposer<Component> {
    @Autowired
    private SewaService sewaService;

    @Wire
    Listbox listboxInventaris;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        loadDataInventaris();
    }

    @Listen("onDoubleClick = #listboxInventaris")
    public void doSelectedInventaris() {
        if (listboxInventaris.getSelectedIndex() == -1)
            return;

        Listitem item = listboxInventaris.getSelectedItem();
        DtoKaryawanTotalLaptop karyawanTotalLaptop = MapperUtil.mappingClass(item.getAttribute("DATA"), DtoKaryawanTotalLaptop.class);
        Map<String, Object> args = new HashMap<>();
        args.put("NIK", karyawanTotalLaptop.getNik());
        Window window = (Window) Executions.createComponents("view/inputInventaris.zul", null, args);
        doAction(window);
    }

    @Listen("onClick = #btnAdd")
    public void doLoadForm() {
        List<Long> niks = new ArrayList<>();
        for (Listitem item: listboxInventaris.getItems()) {
            DtoKaryawanTotalLaptop karyawanTotalLaptop = MapperUtil.mappingClass(item.getAttribute("DATA"), DtoKaryawanTotalLaptop.class);
            niks.add(karyawanTotalLaptop.getNik());
        }
        Map<String, Object> args = new HashMap<>();
        args.put("NIKS", niks);
        Window window = (Window) Executions.createComponents("view/inputInventaris.zul", null, args);
        doAction(window);
    }

    @Listen("onClick = #btnDelete")
    public void doDelete() {
        if (listboxInventaris.getSelectedIndex() == -1)
            return;

        Messagebox.show("Ingin Hapus Data Inventaris ?", "KONFIRMASI", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if ((Integer) event.getData() == Messagebox.YES) {
                    Listitem item = listboxInventaris.getSelectedItem();
                    DtoKaryawanTotalLaptop karyawanTotalLaptop = MapperUtil.mappingClass(item.getAttribute("DATA"), DtoKaryawanTotalLaptop.class);
                    DtoResponse response = sewaService.delete(karyawanTotalLaptop.getNik());
                    if (response.getStatus().equals("SUCCESS")) {
                        loadDataInventaris();
                        Messagebox.show(response.getMsg());
                    }
                }
            }
        });
    }

    public void doAction(Window window) {
        Button btnOk = (Button) window.getFellow("btnOk");
        Button btnCancel = (Button) window.getFellow("btnCancel");
        Listbox listboxSewaLaptop = (Listbox) window.getFellow("listboxSewaLaptop");
        btnOk.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                List<Sewa> sewas = new ArrayList<>();
                for (Listitem item: listboxSewaLaptop.getItems()) {
                    Sewa sewa = MapperUtil.mappingClass(item.getAttribute("DATA"), Sewa.class);
                    sewas.add(sewa);
                }
                if (sewas.size() > 0 ) {
                    DtoResponse response = sewaService.create(sewas);
                    Messagebox.show(response.getMsg());
                    loadDataInventaris();
                    window.detach();
                } else {
                    Messagebox.show("Data Inventarisir Belum Diisi.");
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

    private void loadDataInventaris() {
        listboxInventaris.getItems().clear();
        DtoResponse response = sewaService.findAll();
        if (response.getStatus().equals("SUCCESS")) {
            List<DtoKaryawanTotalLaptop> DtoKaryawanTotalLaptops = (List<DtoKaryawanTotalLaptop>) response.getData();
            for (DtoKaryawanTotalLaptop karyawanTotalLaptop: DtoKaryawanTotalLaptops) {
                Map<String, Object> data = MapperUtil.mappingClass(karyawanTotalLaptop, Map.class);
                Listitem item = new Listitem();
                item.setAttribute("DATA", data);
                item.appendChild(new Listcell(String.valueOf(karyawanTotalLaptop.getNik())));
                String fullName = karyawanTotalLaptop.getFirstName();
                fullName = karyawanTotalLaptop.getLastName() != null ? fullName.concat(" " + karyawanTotalLaptop.getLastName()) : "";
                item.appendChild(new Listcell(fullName));
                item.appendChild(new Listcell(String.valueOf(karyawanTotalLaptop.getTotal())));
                listboxInventaris.appendChild(item);
            }
        }
    }
}

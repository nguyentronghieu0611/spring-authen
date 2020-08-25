package com.bnv.service;

import com.bnv.model.Response;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.StoredProcedureQuery;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class SyncMsgService {
    @Value("#{'${dm.org_Code}'.split(',')}")
    private List<String> dmorg_Code;
    @Value("#{'${dm.doituong}'.split(',')}")
    private List<String> dmdoituong;
    @Value("#{'${dm.gioitinh}'.split(',')}")
    private List<String> dmgioitinh;
    @Value("#{'${dm.tongiao}'.split(',')}")
    private List<String> dmtongiao;
    @Value("#{'${dm.dantoc}'.split(',')}")
    private List<String> dmdantoc;
    @Value("#{'${dm.tiengdantoc}'.split(',')}")
    private List<String> dmtiengdantoc;
    @Value("#{'${dm.vitrituyendung}'.split(',')}")
    private List<String> dmvitrituyendung;
    @Value("#{'${dm.mangach_chucdanh}'.split(',')}")
    private List<String> dmmangach_chucdanh;
    @Value("#{'${dm.bacluong}'.split(',')}")
    private List<String> dmbacluong;
    @Value("#{'${dm.chucvu_chucdanhkn}'.split(',')}")
    private List<String> dmchucvu_chucdanhkn;
    @Value("#{'${dm.loaiphucap}'.split(',')}")
    private List<String> dmloaiphucap;
    @Value("#{'${dm.giaoducphothong}'.split(',')}")
    private List<String> dmgiaoducphothong;
    @Value("#{'${dm.nuocdaotao}'.split(',')}")
    private List<String> dmnuocdaotao;
    @Value("#{'${dm.chucdang}'.split(',')}")
    private List<String> dmchucdang;
    @Value("#{'${dm.lyluanchinhtri}'.split(',')}")
    private List<String> dmlyluanchinhtri;
    @Value("#{'${dm.quanlynhanuoc}'.split(',')}")
    private List<String> dmquanlynhanuoc;
    @Value("#{'${dm.chucdanhkhoahoc}'.split(',')}")
    private List<String> dmchucdanhkhoahoc;
    @Value("#{'${dm.hocvi}'.split(',')}")
    private List<String> dmhocvi;
    @Value("#{'${dm.ngoaingu}'.split(',')}")
    private List<String> dmngoaingu;
    @Value("#{'${dm.trinhdongoaingu}'.split(',')}")
    private List<String> dmtrinhdongoaingu;
    @Value("#{'${dm.trinhdotinhoc}'.split(',')}")
    private List<String> dmtrinhdotinhoc;
    @Value("#{'${dm.chuyennganhdaotao}'.split(',')}")
    private List<String> dmchuyennganhdaotao;
    @Value("#{'${dm.trinhdodaotao}'.split(',')}")
    private List<String> dmtrinhdodaotao;
    @Value("#{'${dm.kettquadanhgia}'.split(',')}")
    private List<String> dmkettquadanhgia;
    @Value("#{'${dm.xeplaoitotnghiep}'.split(',')}")
    private List<String> dmxeplaoitotnghiep;
    @Value("#{'${dm.quocphonganninh}'.split(',')}")
    private List<String> dmquocphonganninh;

    @Value("#{'${dm.donvi.cap1}'.split(',')}")
    private List<String> dmdonvicap1;
    @Value("#{'${dm.donvi.cap2}'.split(',')}")
    private List<String> dmdonvicap2;
    @Value("#{'${dm.donvi.cap3}'.split(',')}")
    private List<String> dmdonvicap3;
    @Value("#{'${dm.donvi.cap4}'.split(',')}")
    private List<String> dmdonvicap4;


    private static String _header = "pkg_parse_json.parse_header_json";
    private static String _thongtinchung = "pkg_parse_json.parse_ns_thongtinchung_json";
    private static String _tuyendungquatrinhcongtac = "pkg_parse_json.parse_ns_tuyendung_qtct_json";
    private static String _quatrinhcongtacs = "pkg_parse_json.parse_ns_quatrinhcongtac_json";
    private static String _luongphucap = "pkg_parse_json.parse_ns_luongphucap_json";
    private static String _luongs = "pkg_parse_json.parse_ns_luong_json";
    private static String _phucaps = "pkg_parse_json.parse_ns_phucap_json";
    private static String _trinhdodaotaoboiduong = "pkg_parse_json.parse_ns_daotaoboiduong_json";
    private static String _daotaoboiduongs = "pkg_parse_json.parse_daotaoboiduong_json";
    private static String _tinhocs = "pkg_parse_json.parse_tinhoc_json";
    private static String _ngoaingus = "pkg_parse_json.parse_ngoaingu_json";
    private static String _thongtinkhac = "pkg_parse_json.parse_ns_thongtinkhac_json";
    private static String _danhgiaphanloais = "pkg_parse_json.parse_danhgiaphanloai_json";
    private static String _xoahosonhansu = "pkg_parse_json.parse_ns_xoahosonhansu_json";
    private static String _sohieucbccvc_bndp = "pkg_select_json.select_ns_hosonhansu_json";

    @Autowired
    EntityMsgService syncMsgService;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public Response callStoreProcedureHeader(JSONObject objheader) {

        String madonvi = "";
        Response response = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            response = syncTieude(tx, em, madonvi, objheader);
            if (response.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return response;
            }
            tx.commit();
            em.close();
            response = new Response("Cập nhật dữ liệu cán bộ, công chức, viên chức thành công", 0);
        } catch (JSONException ex) {
            response = new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            response = new Response("Cập nhật dữ liệu cán bộ, công chức, viên chức không thành công  " + ex.getMessage(), 1);
            //throw ex;
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return response;
    }

    public Response callStoreProcedureServicem0001(String actiontype, int nhansu_id, JSONObject objthongtinchung, JSONObject objtuyendungquatrinhcongtac, JSONObject objluongphucapchucvu, JSONObject objtrinhdodaotaoboiduong, JSONObject objthongtinkhac,
                                                   JSONArray arrquatrinhcongtac, JSONArray arrquatrinhphucap, JSONArray arrquatrinhluong, JSONArray arrtinhoc, JSONArray arrngoaingu, JSONArray arrquatrinhdaotaoboiduong, JSONArray arrketquadanhgia) {
        String madonvi = "";
        Response response = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            if (actiontype.equals("EDIT")) {
                JSONObject objdel = new JSONObject();
                objdel.put("Nhansu_Id", nhansu_id);
                objdel.put("HOSO_CBCCVC", 0);
                objdel.put("DS_QUATRINH_CONGTAC", arrquatrinhcongtac != null ? 1 : 0);
                objdel.put("DS_QUATRINH_PHUCAP", arrquatrinhphucap != null ? 1 : 0);
                objdel.put("DS_QUATRINH_LUONG", arrquatrinhluong != null ? 1 : 0);
                objdel.put("DS_TINHOC", arrtinhoc != null ? 1 : 0);
                objdel.put("DS_NGOAINGU", arrngoaingu != null ? 1 : 0);
                objdel.put("DS_QUATRINH_DAOTAO_BOIDUONG", arrquatrinhdaotaoboiduong != null ? 1 : 0);
                objdel.put("DS_KETQUA_DANHGIA_PHANLOAI", arrketquadanhgia != null ? 1 : 0);
                StoredProcedureQuery deleteAll = syncMsgService.storeProcedures(em, _xoahosonhansu, madonvi, objdel.toString());
                response = getstoreProcedure((String) deleteAll.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }

            response = syncThongtinchung(tx, em, madonvi, nhansu_id, objthongtinchung);
            if (response.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return response;
            }
            nhansu_id = Integer.parseInt(response.getValue());

            if (objtuyendungquatrinhcongtac != null) {
                response = syncTuyendungquatrinhcongtac(tx, em, madonvi, nhansu_id, objtuyendungquatrinhcongtac, arrquatrinhcongtac);
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }

            if (objluongphucapchucvu != null) {
                response = syncLuongphucapchucvu(tx, em, madonvi, nhansu_id, objluongphucapchucvu, arrquatrinhphucap, arrquatrinhluong);
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }

            if (objtrinhdodaotaoboiduong != null) {
                response = syncTrinhdodaotaoboiduong(tx, em, madonvi, nhansu_id, objtrinhdodaotaoboiduong, arrtinhoc, arrngoaingu, arrquatrinhdaotaoboiduong);
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }

            if (objthongtinkhac != null) {
                response = syncThongtinkhac(tx, em, madonvi, nhansu_id, objthongtinkhac);
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }

            if (arrketquadanhgia != null) {
                response = syncKetquadanhgaixeploai(tx, em, madonvi, nhansu_id, arrketquadanhgia);
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }
            tx.commit();
            em.close();
            response = new Response(actiontype.equals("ADD") ? "Thêm mới dữ liệu cán bộ, công chức, viên chức thành công" : "Cập nhật dữ liệu cán bộ, công chức, viên chức thành công", 0);
        } catch (JSONException ex) {
            response = new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            response = new Response("Cập nhật dữ liệu cán bộ, công chức, viên chức không thành công  " + ex.getMessage(), 1);
            //throw ex;
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return response;
    }

    public Response callStoreProcedureServicem0002(String actiontype, int nhansu_id, JSONObject objtuyendungquatrinhcongtac, JSONArray arrquatrinhcongtac) {
        String madonvi = "";
        Response response = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            if (actiontype.equals("EDIT")) {
                JSONObject objdel = new JSONObject();
                objdel.put("Nhansu_Id", nhansu_id);
                objdel.put("HOSO_CBCCVC", 0);
                objdel.put("DS_QUATRINH_CONGTAC", arrquatrinhcongtac != null ? 1 : 0);
                objdel.put("DS_QUATRINH_PHUCAP", 0);
                objdel.put("DS_QUATRINH_LUONG", 0);
                objdel.put("DS_TINHOC", 0);
                objdel.put("DS_NGOAINGU", 0);
                objdel.put("DS_QUATRINH_DAOTAO_BOIDUONG", 0);
                objdel.put("DS_KETQUA_DANHGIA_PHANLOAI", 0);
                StoredProcedureQuery deleteAll = syncMsgService.storeProcedures(em, _xoahosonhansu, madonvi, objdel.toString());
                response = getstoreProcedure((String) deleteAll.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }
            if (objtuyendungquatrinhcongtac != null) {
                response = syncTuyendungquatrinhcongtac(tx, em, madonvi, nhansu_id, objtuyendungquatrinhcongtac, arrquatrinhcongtac);
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }
            tx.commit();
            em.close();
            response = new Response("Cập nhật thông tin về tuyển dụng, quá trình công tác thành công", 0);
        } catch (JSONException ex) {
            response = new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            response = new Response("Cập nhật dữ liệu cán bộ, công chức, viên chức không thành công  " + ex.getMessage(), 1);
            //throw ex;
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return response;
    }

    public Response callStoreProcedureServicem0003(String actiontype, int nhansu_id, JSONObject objluongphucapchucvu, JSONArray arrquatrinhphucap, JSONArray arrquatrinhluong) {
        String madonvi = "";
        Response response = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            if (actiontype.equals("EDIT")) {
                JSONObject objdel = new JSONObject();
                objdel.put("Nhansu_Id", nhansu_id);
                objdel.put("HOSO_CBCCVC", 0);
                objdel.put("DS_QUATRINH_CONGTAC", 0);
                objdel.put("DS_QUATRINH_PHUCAP", arrquatrinhphucap != null ? 1 : 0);
                objdel.put("DS_QUATRINH_LUONG", arrquatrinhluong != null ? 1 : 0);
                objdel.put("DS_TINHOC", 0);
                objdel.put("DS_NGOAINGU", 0);
                objdel.put("DS_QUATRINH_DAOTAO_BOIDUONG", 0);
                objdel.put("DS_KETQUA_DANHGIA_PHANLOAI", 0);
                StoredProcedureQuery deleteAll = syncMsgService.storeProcedures(em, _xoahosonhansu, madonvi, objdel.toString());
                response = getstoreProcedure((String) deleteAll.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }
            if (objluongphucapchucvu != null) {
                response = syncLuongphucapchucvu(tx, em, madonvi, nhansu_id, objluongphucapchucvu, arrquatrinhphucap, arrquatrinhluong);
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }
            tx.commit();
            em.close();
            response = new Response("Cập nhật thông tin về lương, phụ cấp, chức vụ thành công", 0);
        } catch (JSONException ex) {
            response = new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            response = new Response("Cập nhật thông tin về lương, phụ cấp, chức vụ không thành công  " + ex.getMessage(), 1);
            //throw ex;
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return response;
    }

    public Response callStoreProcedureServicem0004(String actiontype, int nhansu_id, JSONObject objtrinhdodaotaoboiduong, JSONArray arrtinhoc, JSONArray arrngoaingu, JSONArray arrquatrinhdaotaoboiduong) {
        String madonvi = "";
        Response response = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            if (actiontype.equals("EDIT")) {
                JSONObject objdel = new JSONObject();
                objdel.put("Nhansu_Id", nhansu_id);
                objdel.put("HOSO_CBCCVC", 0);
                objdel.put("DS_QUATRINH_CONGTAC", 0);
                objdel.put("DS_QUATRINH_PHUCAP", 0);
                objdel.put("DS_QUATRINH_LUONG", 0);
                objdel.put("DS_TINHOC", arrtinhoc != null ? 1 : 0);
                objdel.put("DS_NGOAINGU", arrngoaingu != null ? 1 : 0);
                objdel.put("DS_QUATRINH_DAOTAO_BOIDUONG", arrquatrinhdaotaoboiduong != null ? 1 : 0);
                objdel.put("DS_KETQUA_DANHGIA_PHANLOAI", 0);
                StoredProcedureQuery deleteAll = syncMsgService.storeProcedures(em, _xoahosonhansu, madonvi, objdel.toString());
                response = getstoreProcedure((String) deleteAll.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }
            if (objtrinhdodaotaoboiduong != null) {
                response = syncTrinhdodaotaoboiduong(tx, em, madonvi, nhansu_id, objtrinhdodaotaoboiduong, arrtinhoc, arrngoaingu, arrquatrinhdaotaoboiduong);
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }
            tx.commit();
            em.close();
            response = new Response("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng thành công", 0);
        } catch (JSONException ex) {
            response = new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            response = new Response("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng không thành công  " + ex.getMessage(), 1);
            //throw ex;
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return response;
    }

    public Response callStoreProcedureServicem0005(String actiontype, int nhansu_id, JSONObject objthongtinkhac) {
        String madonvi = "";
        Response response = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            if (objthongtinkhac != null) {
                response = syncThongtinkhac(tx, em, madonvi, nhansu_id, objthongtinkhac);
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }
            tx.commit();
            em.close();
            response = new Response("Cập nhật thông tin khác thành công", 0);
        } catch (JSONException ex) {
            response = new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            response = new Response("Cập nhật thông tin khác không thành công  " + ex.getMessage(), 1);
            //throw ex;
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return response;
    }

    public Response callStoreProcedureServicem0006(String actiontype, int nhansu_id, JSONArray arrketquadanhgia) {
        String madonvi = "";
        Response response = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            if (actiontype.equals("EDIT")) {
                JSONObject objdel = new JSONObject();
                objdel.put("Nhansu_Id", nhansu_id);
                objdel.put("HOSO_CBCCVC", 0);
                objdel.put("DS_QUATRINH_CONGTAC", 0);
                objdel.put("DS_QUATRINH_PHUCAP", 0);
                objdel.put("DS_QUATRINH_LUONG", 0);
                objdel.put("DS_TINHOC", 0);
                objdel.put("DS_NGOAINGU", 0);
                objdel.put("DS_QUATRINH_DAOTAO_BOIDUONG", 0);
                objdel.put("DS_KETQUA_DANHGIA_PHANLOAI", arrketquadanhgia != null ? 1 : 0);
                StoredProcedureQuery deleteAll = syncMsgService.storeProcedures(em, _xoahosonhansu, madonvi, objdel.toString());
                response = getstoreProcedure((String) deleteAll.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }
            if (arrketquadanhgia != null) {
                response = syncKetquadanhgaixeploai(tx, em, madonvi, nhansu_id, arrketquadanhgia);
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return response;
                }
            }
            tx.commit();
            em.close();
            response = new Response("Cập nhật kết quả đánh giá, xếp loại thành công", 0);
        } catch (JSONException ex) {
            response = new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            response = new Response("Cập nhật kết quả đánh giá, xếp loại không thành công  " + ex.getMessage(), 1);
            //throw ex;
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return response;
    }

    public Response syncTieude(EntityTransaction tx, EntityManager em, String madonvi, JSONObject objheader) {
        Response response;
        if (!objheader.getString("Sender_Code").isEmpty() && !seachDanhmuc(dmorg_Code, objheader.getString("Sender_Code")))
            return new Response("Mã đơn vị gửi không tồn tại trên hệ thống!", 1);
        StoredProcedureQuery header = syncMsgService.storeProcedures(em, _header, madonvi, objheader.toString());
        response = getstoreProcedure((String) header.getOutputParameterValue("u_ret"));
        if (response.getErr_code() == 1)
            return response;
        return new Response("Cập nhật thông tin tiêu đề thành công", 0);
    }

    public Response syncThongtinchung(EntityTransaction tx, EntityManager em, String madonvi, int nhansu_id, JSONObject objthongtinchung) {
        Response response;
        boolean madonvisudung = false;
        if (!objthongtinchung.getString("MaDonviSuDung").isEmpty())
            if (seachDanhmuc(dmdonvicap1, objthongtinchung.getString("MaDonviSuDung")))
                madonvisudung = true;
            else if (seachDanhmuc(dmdonvicap2, objthongtinchung.getString("MaDonviSuDung")))
                madonvisudung = true;
            else if (seachDanhmuc(dmdonvicap3, objthongtinchung.getString("MaDonviSuDung")))
                madonvisudung = true;
            else if (seachDanhmuc(dmdonvicap4, objthongtinchung.getString("MaDonviSuDung")))
                madonvisudung = true;

        if (!madonvisudung)
            return new Response("Mã đơn vị sử dụng không có hoặc không đúng với danh mục!", 1);

        boolean madonviquanly = false;
        if (!objthongtinchung.getString("MaDonViQuanLy").isEmpty())
            if (seachDanhmuc(dmdonvicap1, objthongtinchung.getString("MaDonViQuanLy")))
                madonviquanly = true;
            else if (seachDanhmuc(dmdonvicap2, objthongtinchung.getString("MaDonViQuanLy")))
                madonviquanly = true;
            else if (seachDanhmuc(dmdonvicap3, objthongtinchung.getString("MaDonViQuanLy")))
                madonviquanly = true;
            else if (seachDanhmuc(dmdonvicap4, objthongtinchung.getString("MaDonViQuanLy")))
                madonviquanly = true;

        if (!madonviquanly)
            return new Response("Mã đơn vị quản lý không có hoặc không đúng với danh mục!", 1);

        if (!objthongtinchung.getString("PhanLoaiHoSo").isEmpty() && !seachDanhmuc(dmdoituong, objthongtinchung.getString("PhanLoaiHoSo")))
            return new Response("Mã phân loại hồ sơ (đối tượng) không đúng với danh mục!", 1);
        if (!objthongtinchung.getString("GioiTinh").isEmpty() && !seachDanhmuc(dmgioitinh, objthongtinchung.getString("GioiTinh")))
            return new Response("Mã giới tính không đúng với danh mục!", 1);
        if (!objthongtinchung.getString("DanToc").isEmpty() && !seachDanhmuc(dmdantoc, objthongtinchung.getString("DanToc")))
            return new Response("Mã dân tộc không đúng với danh mục!", 1);
        if (!objthongtinchung.getString("TonGiao").isEmpty() && !seachDanhmuc(dmtongiao, objthongtinchung.getString("TonGiao")))
            return new Response("Mã dân tộc không đúng với danh mục!", 1);
        StoredProcedureQuery thongtinchung = syncMsgService.storeProcedures(em, _thongtinchung, madonvi, objthongtinchung.toString());
        response = getstoreProcedure((String) thongtinchung.getOutputParameterValue("u_ret"));
        if (response.getErr_code() == 1)
            return response;
        return new Response("Cập nhật thông tin chung thành công", 0, response.getValue());
    }

    public Response syncTuyendungquatrinhcongtac(EntityTransaction tx, EntityManager em, String madonvi, int nhansu_id, JSONObject objtuyendungquatrinhcongtac, JSONArray arrquatrinhcongtac) {
        Response response;
        if (arrquatrinhcongtac != null) {
            for (int i = 0; i < arrquatrinhcongtac.length(); i++) {
                JSONObject quatrinhcongtac = arrquatrinhcongtac.getJSONObject(i);
                quatrinhcongtac.put("NhanSu_Id", nhansu_id);
                StoredProcedureQuery quatrinhcongtacQuery = syncMsgService.storeProcedures(em, _quatrinhcongtacs, madonvi, quatrinhcongtac.toString());
                response = getstoreProcedure((String) quatrinhcongtacQuery.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1)
                    return response;
            }
            objtuyendungquatrinhcongtac.remove("DS_QUATRINH_CONGTAC");
        }
        if (!objtuyendungquatrinhcongtac.isEmpty()) {
            objtuyendungquatrinhcongtac.put("NhanSu_Id", nhansu_id);
            if (!objtuyendungquatrinhcongtac.getString("ViTriTuyenDung").isEmpty() && !seachDanhmuc(dmvitrituyendung, objtuyendungquatrinhcongtac.getString("ViTriTuyenDung")))
                return new Response("Mã vị trí tuyển dụng không đúng với danh mục!", 1);
            StoredProcedureQuery tuyendungquatrinhcongtac = syncMsgService.storeProcedures(em, _tuyendungquatrinhcongtac, madonvi, objtuyendungquatrinhcongtac.toString());
            response = getstoreProcedure((String) tuyendungquatrinhcongtac.getOutputParameterValue("u_ret"));
            if (response.getErr_code() == 1)
                return response;
        }
        return new Response("Cập nhật thông tin tuyển dụng quá trình công tác thành công", 0);
    }

    public Response syncLuongphucapchucvu(EntityTransaction tx, EntityManager em, String madonvi, int nhansu_id, JSONObject objluongphucapchucvu, JSONArray arrquatrinhphucap, JSONArray arrquatrinhluong) {
        Response response;
        if (arrquatrinhphucap != null) {
            for (int i = 0; i < arrquatrinhphucap.length(); i++) {
                JSONObject quatrinhphucap = arrquatrinhphucap.getJSONObject(i);
                quatrinhphucap.put("NhanSu_Id", nhansu_id);
                if (!quatrinhphucap.getString("LoaiPhuCap").isEmpty() && !seachDanhmuc(dmloaiphucap, quatrinhphucap.getString("LoaiPhuCap")))
                    return new Response("Mã loại phụ cấp không đúng với danh mục!", 1);

                StoredProcedureQuery quatrinhphucapQuery = syncMsgService.storeProcedures(em, _phucaps, madonvi, quatrinhphucap.toString());
                response = getstoreProcedure((String) quatrinhphucapQuery.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1)
                    return response;
            }
            objluongphucapchucvu.remove("DS_QUATRINH_PHUCAP");
        }
        if (arrquatrinhluong != null) {
            for (int i = 0; i < arrquatrinhluong.length(); i++) {
                JSONObject quatrinhluong = arrquatrinhluong.getJSONObject(i);
                quatrinhluong.put("NhanSu_Id", nhansu_id);
                if (!quatrinhluong.getString("Ngach").isEmpty() && !seachDanhmuc(dmmangach_chucdanh, quatrinhluong.getString("Ngach")))
                    return new Response("Mã ngạch chức danh không đúng với danh mục!", 1);
                if (!quatrinhluong.getString("BacLuong").isEmpty() && !seachDanhmuc(dmbacluong, quatrinhluong.getString("BacLuong")))
                    return new Response("Mã bậc lương không đúng với danh mục!", 1);
                StoredProcedureQuery quatrinhluongQuery = syncMsgService.storeProcedures(em, _luongs, madonvi, quatrinhluong.toString());
                response = getstoreProcedure((String) quatrinhluongQuery.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1)
                    return response;
            }
            objluongphucapchucvu.remove("DS_QUATRINH_LUONG");
        }
        if (!objluongphucapchucvu.isEmpty()) {
            objluongphucapchucvu.put("NhanSu_Id", nhansu_id);
            if (!objluongphucapchucvu.getString("MaNgachChucDanh").isEmpty() && !seachDanhmuc(dmmangach_chucdanh, objluongphucapchucvu.getString("MaNgachChucDanh")))
                return new Response("Mã ngạch chức danh không đúng với danh mục!", 1);
            if (!objluongphucapchucvu.getString("BacLuong").isEmpty() && !seachDanhmuc(dmbacluong, objluongphucapchucvu.getString("BacLuong")))
                return new Response("Mã bậc lương không đúng với danh mục!", 1);

            if (!objluongphucapchucvu.getString("ChucVu").isEmpty() && !seachDanhmuc(dmchucvu_chucdanhkn, objluongphucapchucvu.getString("ChucVu")))
                return new Response("Mã chức vụ không đúng với danh mục!", 1);
            if (!objluongphucapchucvu.getString("ChucVuChucDanhKiemNhiem").isEmpty() && !seachDanhmuc(dmchucvu_chucdanhkn, objluongphucapchucvu.getString("ChucVuChucDanhKiemNhiem")))
                return new Response("Mã chức danh kiêm nhiệm không đúng với danh mục!", 1);
            StoredProcedureQuery luongphucapchucvu = syncMsgService.storeProcedures(em, _luongphucap, madonvi, objluongphucapchucvu.toString());
            response = getstoreProcedure((String) luongphucapchucvu.getOutputParameterValue("u_ret"));
            if (response.getErr_code() == 1)
                return response;
        }
        return new Response("Cập nhật thông tin lương, phụ cấp, chức vụ thành công", 0);
    }

    public Response syncTrinhdodaotaoboiduong(EntityTransaction tx, EntityManager em, String madonvi, int nhansu_id, JSONObject objtrinhdodaotaoboiduong, JSONArray arrtinhoc, JSONArray jarrngoaingu, JSONArray arrquatrinhdaotaoboiduong) {
        Response response;
        if (arrtinhoc != null) {
            for (int i = 0; i < arrtinhoc.length(); i++) {
                JSONObject tinhoc = arrtinhoc.getJSONObject(i);
                tinhoc.put("NhanSu_Id", nhansu_id);
                if (!tinhoc.getString("TrinhDo").isEmpty() && !seachDanhmuc(dmtrinhdotinhoc, tinhoc.getString("TrinhDo")))
                    return new Response("Mã trình độ tin học không đúng với danh mục!", 1);
                StoredProcedureQuery ngoainguQuery = syncMsgService.storeProcedures(em, _tinhocs, madonvi, tinhoc.toString());
                response = getstoreProcedure((String) ngoainguQuery.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1)
                    return response;
            }
            objtrinhdodaotaoboiduong.remove("DS_TINHOC");
        }

        if (jarrngoaingu != null) {
            for (int i = 0; i < jarrngoaingu.length(); i++) {
                JSONObject ngoaingu = jarrngoaingu.getJSONObject(i);
                ngoaingu.put("NhanSu_Id", nhansu_id);
                if (!ngoaingu.getString("MaNgoaiNgu").isEmpty() && !seachDanhmuc(dmngoaingu, ngoaingu.getString("MaNgoaiNgu")))
                    return new Response("Mã ngoại ngữ không đúng với danh mục!", 1);
                if (!ngoaingu.getString("TrinhDo").isEmpty() && !seachDanhmuc(dmtrinhdongoaingu, ngoaingu.getString("TrinhDo")))
                    return new Response("Mã trình độ ngoại ngữ không đúng với danh mục!", 1);
                StoredProcedureQuery ngoainguQuery = syncMsgService.storeProcedures(em, _ngoaingus, madonvi, ngoaingu.toString());
                response = getstoreProcedure((String) ngoainguQuery.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1)
                    return response;
            }
            objtrinhdodaotaoboiduong.remove("DS_NGOAINGU");
        }
        if (arrquatrinhdaotaoboiduong != null) {
            for (int i = 0; i < arrquatrinhdaotaoboiduong.length(); i++) {
                JSONObject quatrinhdaotaoboiduong = arrquatrinhdaotaoboiduong.getJSONObject(i);
                quatrinhdaotaoboiduong.put("NhanSu_Id", nhansu_id);
                if (!quatrinhdaotaoboiduong.getString("ChuyenNganhDaoTao").isEmpty() && !seachDanhmuc(dmchuyennganhdaotao, quatrinhdaotaoboiduong.getString("ChuyenNganhDaoTao")))
                    return new Response("Mã chuyên ngành đào tạo không đúng với danh mục!", 1);
                if (!quatrinhdaotaoboiduong.getString("TrinhDoDaoTao").isEmpty() && !seachDanhmuc(dmtrinhdodaotao, quatrinhdaotaoboiduong.getString("TrinhDoDaoTao")))
                    return new Response("Mã  trình độ đào tạo không đúng với danh mục!", 1);
                if (!quatrinhdaotaoboiduong.getString("XepLoaiTotNghiep").isEmpty() && !seachDanhmuc(dmxeplaoitotnghiep, quatrinhdaotaoboiduong.getString("XepLoaiTotNghiep")))
                    return new Response("Mã xếp loại tốt nghiệp không đúng với danh mục!", 1);
                if (!quatrinhdaotaoboiduong.getString("NuocDaoTao").isEmpty() && !seachDanhmuc(dmnuocdaotao, quatrinhdaotaoboiduong.getString("NuocDaoTao")))
                    return new Response("Mã nước đào tạo không đúng với danh mục!", 1);
                StoredProcedureQuery quatrinhdaotaoboiduongQuery = syncMsgService.storeProcedures(em, _daotaoboiduongs, madonvi, quatrinhdaotaoboiduong.toString());
                response = getstoreProcedure((String) quatrinhdaotaoboiduongQuery.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1)
                    return response;
            }
            objtrinhdodaotaoboiduong.remove("DS_QUATRINH_DAOTAO_BOIDUONG");
        }
        if (!objtrinhdodaotaoboiduong.isEmpty()) {
            objtrinhdodaotaoboiduong.put("NhanSu_Id", nhansu_id);
            if (!objtrinhdodaotaoboiduong.getString("HocVanPhoThong").isEmpty() && !seachDanhmuc(dmgiaoducphothong, objtrinhdodaotaoboiduong.getString("HocVanPhoThong")))
                return new Response("Mã học vấn phổ thông không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("TrinhDoLyLuanChinhTri").isEmpty() && !seachDanhmuc(dmlyluanchinhtri, objtrinhdodaotaoboiduong.getString("TrinhDoLyLuanChinhTri")))
                return new Response("Mã trình độ lý luận chính trị không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("TrinhDoQuanLyNhaNuoc").isEmpty() && !seachDanhmuc(dmquanlynhanuoc, objtrinhdodaotaoboiduong.getString("TrinhDoQuanLyNhaNuoc")))
                return new Response("Mã trình độ quản lý nhà nước không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("BoiDuongQuocPhongAnNinh").isEmpty() && !seachDanhmuc(dmquocphonganninh, objtrinhdodaotaoboiduong.getString("BoiDuongQuocPhongAnNinh")))
                return new Response("Bồi dưỡng quốc phòng an ninh không đúng với quy định (0 hoặc 1)!", 1);
            if (!objtrinhdodaotaoboiduong.getString("MaChucDanhKhoaHoc").isEmpty() && !seachDanhmuc(dmchucdanhkhoahoc, objtrinhdodaotaoboiduong.getString("MaChucDanhKhoaHoc")))
                return new Response("Mã chức danh khoa học không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("MaHocVi").isEmpty() && !seachDanhmuc(dmhocvi, objtrinhdodaotaoboiduong.getString("MaHocVi")))
                return new Response("Mã học vị không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("TiengDanTocThieuSo").isEmpty() && !seachDanhmuc(dmtiengdantoc, objtrinhdodaotaoboiduong.getString("TiengDanTocThieuSo")))
                return new Response("Mã tiếng dân tộc không đúng với danh mục!", 1);
            StoredProcedureQuery trinhdodaotaoboiduong = syncMsgService.storeProcedures(em, _trinhdodaotaoboiduong, madonvi, objtrinhdodaotaoboiduong.toString());
            response = getstoreProcedure((String) trinhdodaotaoboiduong.getOutputParameterValue("u_ret"));
            if (response.getErr_code() == 1)
                return response;
        }
        return new Response("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng, thành công", 0);
    }

    public Response syncThongtinkhac(EntityTransaction tx, EntityManager em, String madonvi, int nhansu_id, JSONObject objthongtinkhac) {
        Response response;
        objthongtinkhac.put("NhanSu_Id", nhansu_id);
        if (!objthongtinkhac.getString("ChucVuDang").isEmpty() && !seachDanhmuc(dmchucdang, objthongtinkhac.getString("ChucVuDang")))
            return new Response("Mã chức vụ đảng không đúng với danh mục!", 1);
        StoredProcedureQuery thongtinkhacQuery = syncMsgService.storeProcedures(em, _thongtinkhac, madonvi, objthongtinkhac.toString());
        response = getstoreProcedure((String) thongtinkhacQuery.getOutputParameterValue("u_ret"));
        if (response.getErr_code() == 1)
            return response;
        return new Response("Cập nhật thông tin khác thành công", 0);
    }

    public Response syncKetquadanhgaixeploai(EntityTransaction tx, EntityManager em, String madonvi, int nhansu_id, JSONArray arrketquadanhgia) {
        Response response;
        for (int i = 0; i < arrketquadanhgia.length(); i++) {
            JSONObject ketquadanhgia = arrketquadanhgia.getJSONObject(i);
            ketquadanhgia.put("NhanSu_Id", nhansu_id);
            if (!ketquadanhgia.getString("KetQuaDanhGia").isEmpty() && !seachDanhmuc(dmkettquadanhgia, ketquadanhgia.getString("KetQuaDanhGia")))
                return new Response("Mã kết quả đánh giá không đúng với danh mục!", 1);
            StoredProcedureQuery ketquadanhgiaphanloaiQuery = syncMsgService.storeProcedures(em, _danhgiaphanloais, madonvi, ketquadanhgia.toString());
            response = getstoreProcedure((String) ketquadanhgiaphanloaiQuery.getOutputParameterValue("u_ret"));
            if (response.getErr_code() == 1)
                return response;
        }
        return new Response("Cập nhật thông tin kết quả xếp loại đánh giá thành công", 0);
    }

    //kiểm tra danh mục
    public boolean seachDanhmuc(List<String> list, String search) {
        Iterable<String> result = Iterables.filter(list, Predicates.containsPattern(search));
        List<String> stringList = Lists.newArrayList(result.iterator());
        if (stringList.size() > 0)
            return true;
        else return false;
    }
    //kiểm tra số hiệu cbccvc_bndp
    public Response selSohieuCbccvc_Bndp(String madonvi, String sohieucbccvc_bndp) {
        Response response = null;
        try {
            response = getstoreProcedure(_sohieucbccvc_bndp, madonvi, sohieucbccvc_bndp);
        } catch (Exception ex) {
            response = new Response("Tìm kiếm hồ sơ cbccvc bndp " + ex.getMessage(), 1);
        }
        return response;
    }
    // xử lý json trả về
    public Response getstoreProcedure(String storename, String madonvi, String json) {
        try {
            JSONObject retjson = new JSONObject(syncMsgService.callStoreProcedure(storename, madonvi, json));
            String mess = retjson.getString("MSG_TEXT");
            int err = retjson.getInt("MSG_CODE");
            String value = retjson.getString("VAL");
            return new Response(mess, err == 1 ? 0 : 1, value);
        } catch (Exception ex) {
            return new Response(ex.getMessage(), 1);
        }
    }
    // xử lý json trả về
    public Response getstoreProcedure(String json) {
        try {
            JSONObject retjson = new JSONObject(json);
            String mess = retjson.getString("MSG_TEXT");
            int err = retjson.getInt("MSG_CODE");
            String value = retjson.getString("VAL");
            return new Response(mess, err == 1 ? 0 : 1, err == 1 ? value : null);
        } catch (Exception ex) {
            return new Response(ex.getMessage(), 1);
        }
    }
    public boolean isThisDateValid(String dateToValidate){
        if(dateToValidate == null){
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateToValidate);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

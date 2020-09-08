package com.bnv.service;

import com.bnv.model.SyncResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@Service
public class SyncMsgService {
    @Autowired
    EntityMsgService syncMsgService;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private SyncService syncService;
    private String madonvi = "";

    public SyncResponse updateServiceM0001(String actiontype, int nhansu_id, JSONObject objheader, JSONObject objthongtinchung, JSONObject objtuyendungquatrinhcongtac, JSONObject objluongphucapchucvu, JSONObject objtrinhdodaotaoboiduong, JSONObject objthongtinkhac,
                                           JSONArray arrquatrinhcongtac, JSONArray arrquatrinhphucap, JSONArray arrquatrinhluong, JSONArray arrtinhoc, JSONArray arrngoaingu, JSONArray arrquatrinhdaotaoboiduong, JSONArray arrketquadanhgia) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }

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
                syncresponse = syncService.syncXoadulieu(em, madonvi, objdel.toString());
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            syncresponse = syncService.syncThongtinchung(em, madonvi, nhansu_id, objthongtinchung);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }
            nhansu_id = Integer.parseInt(syncresponse.getValue());

            if (objtuyendungquatrinhcongtac != null && !objtuyendungquatrinhcongtac.isEmpty()) {
                syncresponse = syncService.syncTuyendungquatrinhcongtac(em, madonvi, nhansu_id, objtuyendungquatrinhcongtac, arrquatrinhcongtac);
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }

            if (objluongphucapchucvu != null && !objluongphucapchucvu.isEmpty()) {
                syncresponse = syncService.syncLuongphucapchucvu(em, madonvi, nhansu_id, objluongphucapchucvu, arrquatrinhphucap, arrquatrinhluong);
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }

            if (objtrinhdodaotaoboiduong != null && !objtrinhdodaotaoboiduong.isEmpty()) {
                syncresponse = syncService.syncTrinhdodaotaoboiduong(em, madonvi, nhansu_id, objtrinhdodaotaoboiduong, arrtinhoc, arrngoaingu, arrquatrinhdaotaoboiduong);
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }

            if (objthongtinkhac != null && !objthongtinkhac.isEmpty()) {
                syncresponse = syncService.syncThongtinkhac(em, madonvi, nhansu_id, objthongtinkhac);
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    if (em != null && em.isOpen()) em.close();
                    return syncresponse;
                }
            }

            if (arrketquadanhgia != null && !arrketquadanhgia.isEmpty()) {
                syncresponse = syncService.syncKetquadanhgaixeploai(em, madonvi, nhansu_id, arrketquadanhgia);
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            tx.commit();
            syncresponse = new SyncResponse(actiontype.equals("ADD") ? "Thêm mới dữ liệu cán bộ, công chức, viên chức thành công" : "Cập nhật dữ liệu cán bộ, công chức, viên chức thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json hồ sơ cán bộ, công chức, viên chức không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Cập nhật dữ liệu cán bộ, công chức, viên chức không thành công: " + ex.getMessage() + "!", 1);
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    public SyncResponse deleteServiceM0001(int nhansu_id, JSONObject objheader) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }
            JSONObject objdel = new JSONObject();
            objdel.put("Nhansu_Id", nhansu_id);
            objdel.put("HOSO_CBCCVC", 1);
            objdel.put("DS_QUATRINH_CONGTAC", 1);
            objdel.put("DS_QUATRINH_PHUCAP", 1);
            objdel.put("DS_QUATRINH_LUONG", 1);
            objdel.put("DS_TINHOC", 1);
            objdel.put("DS_NGOAINGU", 1);
            objdel.put("DS_QUATRINH_DAOTAO_BOIDUONG", 1);
            objdel.put("DS_KETQUA_DANHGIA_PHANLOAI", 1);
            syncresponse = syncService.syncXoadulieu(em, madonvi, objdel.toString());
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                return syncresponse;
            }

            tx.commit();
            syncresponse = new SyncResponse("Xóa dữ liệu cán bộ, công chức, viên chức thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json hồ sơ cán bộ, công chức, viên chức không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Xóa dữ liệu cán bộ, công chức, viên chức không thành công: " + ex.getMessage() + "!", 1);
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }


    public SyncResponse updateServicem0002(String actiontype, int nhansu_id, JSONObject objheader, JSONObject objtuyendungquatrinhcongtac, JSONArray arrquatrinhcongtac) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }

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
                syncresponse = syncService.syncXoadulieu(em, madonvi, objdel.toString());
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            if (objtuyendungquatrinhcongtac != null && !objtuyendungquatrinhcongtac.isEmpty()) {
                syncresponse = syncService.syncTuyendungquatrinhcongtac(em, madonvi, nhansu_id, objtuyendungquatrinhcongtac, arrquatrinhcongtac);
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            tx.commit();
            syncresponse = new SyncResponse("Cập nhật thông tin về tuyển dụng, quá trình công tác thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json tuyển dụng, quá trình công tác không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Cập nhật thông tin về tuyển dụng, quá trình công tác không thành công: " + ex.getMessage() + "!", 1);

        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    public SyncResponse deleteServiceM0002(int nhansu_id, JSONObject objheader, JSONArray arrquatrinhcongtac) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }
            JSONObject objdel = new JSONObject();
            objdel.put("Nhansu_Id", nhansu_id);
            objdel.put("HOSO_CBCCVC", 0);
            objdel.put("DS_QUATRINH_CONGTAC", arrquatrinhcongtac.length() >= 0 ? 1 : 0);
            objdel.put("DS_QUATRINH_PHUCAP", 0);
            objdel.put("DS_QUATRINH_LUONG", 0);
            objdel.put("DS_TINHOC", 0);
            objdel.put("DS_NGOAINGU", 0);
            objdel.put("DS_QUATRINH_DAOTAO_BOIDUONG", 0);
            objdel.put("DS_KETQUA_DANHGIA_PHANLOAI", 0);
            syncresponse = syncService.syncXoadulieu(em, madonvi, objdel.toString());
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                return syncresponse;
            }

            tx.commit();
            syncresponse = new SyncResponse("Xóa thông tin về tuyển dụng, quá trình công tác thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json tuyển dụng, quá trình công tác không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Xóa thông tin về tuyển dụng, quá trình công tác không thành công: " + ex.getMessage() + "!", 1);
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    public SyncResponse updateServicem0003(String actiontype, int nhansu_id, JSONObject objheader, JSONObject objluongphucapchucvu, JSONArray arrquatrinhphucap, JSONArray arrquatrinhluong) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }

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
                syncresponse = syncService.syncXoadulieu(em, madonvi, objdel.toString());
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            if (objluongphucapchucvu != null && !objluongphucapchucvu.isEmpty()) {
                syncresponse = syncService.syncLuongphucapchucvu(em, madonvi, nhansu_id, objluongphucapchucvu, arrquatrinhphucap, arrquatrinhluong);
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            tx.commit();
            syncresponse = new SyncResponse("Cập nhật thông tin về lương, phụ cấp, chức vụ thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json lương, phụ cấp, chức vụ không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Cập nhật thông tin về lương, phụ cấp, chức vụ không thành công: " + ex.getMessage() + "!", 1);
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    public SyncResponse deleteServiceM0003(int nhansu_id, JSONObject objheader, JSONArray arrquatrinhphucap, JSONArray arrquatrinhluong) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }
            JSONObject objdel = new JSONObject();
            objdel.put("Nhansu_Id", nhansu_id);
            objdel.put("HOSO_CBCCVC", 0);
            objdel.put("DS_QUATRINH_CONGTAC", 0);
            objdel.put("DS_QUATRINH_PHUCAP", arrquatrinhphucap.length() >= 0 ? 1 : 0);
            objdel.put("DS_QUATRINH_LUONG", arrquatrinhluong.length() >= 0 ? 1 : 0);
            objdel.put("DS_TINHOC", 0);
            objdel.put("DS_NGOAINGU", 0);
            objdel.put("DS_QUATRINH_DAOTAO_BOIDUONG", 0);
            objdel.put("DS_KETQUA_DANHGIA_PHANLOAI", 0);
            syncresponse = syncService.syncXoadulieu(em, madonvi, objdel.toString());
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                return syncresponse;
            }

            tx.commit();
            syncresponse = new SyncResponse("Xóa thông thông tin về lương, phụ cấp, chức vụ thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json lương, phụ cấp, chức vụ không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Xóa thông tin về lương, phụ cấp, chức vụ không thành công: " + ex.getMessage() + "!", 1);
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    public SyncResponse updateServicem0004(String actiontype, int nhansu_id, JSONObject objheader, JSONObject objtrinhdodaotaoboiduong, JSONArray arrtinhoc, JSONArray arrngoaingu, JSONArray arrquatrinhdaotaoboiduong) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }

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
                syncresponse = syncService.syncXoadulieu(em, madonvi, objdel.toString());
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            if (objtrinhdodaotaoboiduong != null && !objtrinhdodaotaoboiduong.isEmpty()) {
                syncresponse = syncService.syncTrinhdodaotaoboiduong(em, madonvi, nhansu_id, objtrinhdodaotaoboiduong, arrtinhoc, arrngoaingu, arrquatrinhdaotaoboiduong);
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            tx.commit();
            syncresponse = new SyncResponse("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json trình độ, đào tao, bồi dưỡng không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng không thành công: " + ex.getMessage() + "!", 1);

        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    public SyncResponse deleteServiceM0004(int nhansu_id, JSONObject objheader, JSONArray arrtinhoc, JSONArray arrngoaingu, JSONArray arrquatrinhdaotaoboiduong) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }
            JSONObject objdel = new JSONObject();
            objdel.put("Nhansu_Id", nhansu_id);
            objdel.put("HOSO_CBCCVC", 0);
            objdel.put("DS_QUATRINH_CONGTAC", 0);
            objdel.put("DS_QUATRINH_PHUCAP", 0);
            objdel.put("DS_QUATRINH_LUONG", 0);
            objdel.put("DS_TINHOC", arrtinhoc.length() >= 0 ? 1 : 0);
            objdel.put("DS_NGOAINGU", arrngoaingu.length() >= 0 ? 1 : 0);
            objdel.put("DS_QUATRINH_DAOTAO_BOIDUONG", arrquatrinhdaotaoboiduong.length() >= 0 ? 1 : 0);
            objdel.put("DS_KETQUA_DANHGIA_PHANLOAI", 0);
            syncresponse = syncService.syncXoadulieu(em, madonvi, objdel.toString());
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                return syncresponse;
            }

            tx.commit();
            syncresponse = new SyncResponse("Xóa thông tin trình độ, đào tạo, bồi dưỡng thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json trình độ, đào tao, bồi dưỡng không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Xóa thông tin trình độ, đào tạo, bồi dưỡng không thành công: " + ex.getMessage() + "!", 1);
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    public SyncResponse updateServicem0005(String actiontype, int nhansu_id, JSONObject objheader, JSONObject objthongtinkhac) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }

            if (objthongtinkhac != null && !objthongtinkhac.isEmpty()) {
                syncresponse = syncService.syncThongtinkhac(em, madonvi, nhansu_id, objthongtinkhac);
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            tx.commit();
            syncresponse = new SyncResponse("Cập nhật thông tin khác thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json thông tin khác không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Cập nhật thông tin khác không thành công: " + ex.getMessage() + "!", 1);

        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    public SyncResponse deleteServiceM0005(int nhansu_id, JSONObject objheader) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }
            tx.commit();
            syncresponse = new SyncResponse("Không thể xóa thông tin khác do dữ liệu cùng thông tin hồ sơ chung, bạn có thể cập nhật giá trị rỗng cho thông tin khác!", 1);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json trình độ, đào tao, bồi dưỡng không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Xóa thông tin khác không thành công: " + ex.getMessage() + "!", 1);
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    public SyncResponse updateServicem0006(String actiontype, int nhansu_id, JSONObject objheader, JSONArray arrketquadanhgia) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }

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
                syncresponse = syncService.syncXoadulieu(em, madonvi, objdel.toString());
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            if (arrketquadanhgia != null && !arrketquadanhgia.isEmpty()) {
                syncresponse = syncService.syncKetquadanhgaixeploai(em, madonvi, nhansu_id, arrketquadanhgia);
                if (syncresponse.getErr_code() == 1) {
                    tx.rollback();
                    return syncresponse;
                }
            }
            tx.commit();
            syncresponse = new SyncResponse("Cập nhật kết quả đánh giá, xếp loại thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json kết quả đánh giá xếp loại không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Cập nhật kết quả đánh giá, xếp loại không thành công: " + ex.getMessage() + "!", 1);

        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    public SyncResponse deleteServiceM0006(int nhansu_id, JSONObject objheader, JSONArray arrketquadanhgia) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            syncresponse = syncService.syncTieude(em, madonvi, objheader);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                if (em != null && em.isOpen()) em.close();
                return syncresponse;
            }
            JSONObject objdel = new JSONObject();
            objdel.put("Nhansu_Id", nhansu_id);
            objdel.put("HOSO_CBCCVC", 0);
            objdel.put("DS_QUATRINH_CONGTAC", 0);
            objdel.put("DS_QUATRINH_PHUCAP", 0);
            objdel.put("DS_QUATRINH_LUONG", 0);
            objdel.put("DS_TINHOC", 0);
            objdel.put("DS_NGOAINGU", 0);
            objdel.put("DS_QUATRINH_DAOTAO_BOIDUONG", 0);
            objdel.put("DS_KETQUA_DANHGIA_PHANLOAI", arrketquadanhgia.length() >= 0 ? 1 : 0);
            syncresponse = syncService.syncXoadulieu(em, madonvi, objdel.toString());
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                return syncresponse;
            }

            tx.commit();
            syncresponse = new SyncResponse("Xóa kết quả đánh giá, xếp loại thành công", 0);
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json kết quả đánh giá xếp loại không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Xóa kết quả đánh giá, xếp loại thành công không thành công: " + ex.getMessage() + "!", 1);
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }

    //kiểm tra số hiệu cbccvc_bndp
    public SyncResponse callStoreProcedureServicemId(String madonvi, String sohieucbccvc_bndp) {
        SyncResponse syncresponse = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            syncresponse = syncService.syncSohieuCbccvcBndp(em, madonvi, sohieucbccvc_bndp);
            if (syncresponse.getErr_code() == 1) {
                tx.rollback();
                return syncresponse;
            }
            tx.commit();
            syncresponse = new SyncResponse("Kiểm tra hồ sơ nhân sự không thành công thành công", 0, syncresponse.getValue());
        } catch (JSONException ex) {
            syncresponse = new SyncResponse("Cấu trúc gói tin json kiểm tra số hiệu cán bộ, công chức, viên chức bộ nghành địa phương không đúng định dạng quy định!", 1);
        } catch (RuntimeException ex) {
            syncresponse = new SyncResponse("Kiểm tra hồ sơ nhân sự không thành không thành công: " + ex.getMessage() + "!", 1);
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return syncresponse;
    }
}

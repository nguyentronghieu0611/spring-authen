package com.bnv.service;

import com.bnv.model.SyncResponse;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class SyncService {
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
    @Value("#{'${dm.hinhthuchuongphucap}'.split(',')}")
    private List<String> dmhinhthuchuongphucap;
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
    @Value("#{'${dm.trinhdochuyenmondaotao}'.split(',')}")
    private List<String> dmtrinhdochuyenmondaotao;
    @Value("#{'${dm.chuyennganhdaotao}'.split(',')}")
    private List<String> dmchuyennganhdaotao;
    //    @Value("#{'${dm.trinhdodaotao}'.split(',')}")
//    private List<String> dmtrinhdodaotao;
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

    public SyncResponse syncTieude(EntityManager em, String madonvi, JSONObject objheader) {
        if (!objheader.getString("Sender_Code").isEmpty() && !validateCategory(dmorg_Code, objheader.getString("Sender_Code")))
            return new SyncResponse("Mã đơn vị gửi không tồn tại trên hệ thống!", 1);
        SyncResponse syncresponse = getstoreProcedure(em, _header, madonvi, objheader.toString());
        if (syncresponse.getErr_code() == 1)
            return new SyncResponse(String.format("Cập nhật thông tin header không thành công: %s!", syncresponse.getMessage()), 1);
        return new SyncResponse(0);
    }

    public SyncResponse syncThongtinchung(EntityManager em, String madonvi, int nhansu_id, JSONObject objthongtinchung) {
        SyncResponse syncresponse;
        boolean madonvisudung = false;
        if (objthongtinchung.getString("MaDonviSuDung").isEmpty())
            madonvisudung = false;
        else {
            if (validateCategory(dmdonvicap1, objthongtinchung.getString("MaDonviSuDung")))
                madonvisudung = true;
            else if (validateCategory(dmdonvicap2, objthongtinchung.getString("MaDonviSuDung")))
                madonvisudung = true;
            else if (validateCategory(dmdonvicap3, objthongtinchung.getString("MaDonviSuDung")))
                madonvisudung = true;
            else if (validateCategory(dmdonvicap4, objthongtinchung.getString("MaDonviSuDung")))
                madonvisudung = true;
        }
        if (!madonvisudung)
            return new SyncResponse("Mã đơn vị sử dụng không có hoặc không đúng với danh mục!", 1);

        boolean madonviquanly = false;
        if (objthongtinchung.getString("MaDonViQuanLy").isEmpty())
            madonviquanly = false;
        else {
            if (validateCategory(dmdonvicap1, objthongtinchung.getString("MaDonViQuanLy")))
                madonviquanly = true;
            else if (validateCategory(dmdonvicap2, objthongtinchung.getString("MaDonViQuanLy")))
                madonviquanly = true;
            else if (validateCategory(dmdonvicap3, objthongtinchung.getString("MaDonViQuanLy")))
                madonviquanly = true;
            else if (validateCategory(dmdonvicap4, objthongtinchung.getString("MaDonViQuanLy")))
                madonviquanly = true;
        }
        if (!madonviquanly)
            return new SyncResponse("Mã đơn vị quản lý không có hoặc không đúng với danh mục!", 1);

        if (objthongtinchung.getString("SoHieuCBCCVC").isEmpty())
            return new SyncResponse("Số hiệu cán bộ, công chức, viên chức không có hoặc rỗng!", 1);
        if (objthongtinchung.getString("HoVaTen").isEmpty())
            return new SyncResponse("Họ tên cán bộ, công chức, viên chức không có hoặc rỗng!", 1);
        if (!objthongtinchung.getString("PhanLoaiHoSo").isEmpty() && !validateCategory(dmdoituong, objthongtinchung.getString("PhanLoaiHoSo")))
            return new SyncResponse("Mã phân loại hồ sơ (đối tượng) không đúng với danh mục!", 1);
        if (!objthongtinchung.getString("GioiTinh").isEmpty() && !validateCategory(dmgioitinh, objthongtinchung.getString("GioiTinh")))
            return new SyncResponse("Mã giới tính không đúng với danh mục!", 1);
        if (!objthongtinchung.getString("DanToc").isEmpty() && !validateCategory(dmdantoc, objthongtinchung.getString("DanToc")))
            return new SyncResponse("Mã dân tộc không đúng với danh mục!", 1);
        if (!objthongtinchung.getString("TonGiao").isEmpty() && !validateCategory(dmtongiao, objthongtinchung.getString("TonGiao")))
            return new SyncResponse("Mã tôn giáo không đúng với danh mục!", 1);
        syncresponse = getstoreProcedure(em, _thongtinchung, madonvi, objthongtinchung.toString());
        if (syncresponse.getErr_code() == 1)
            return new SyncResponse(String.format("Cập nhật thông tin chung không thành công: %s!", syncresponse.getMessage()), 1);
        return new SyncResponse(0, syncresponse.getValue());
    }

    public SyncResponse syncTuyendungquatrinhcongtac(EntityManager em, String madonvi, int nhansu_id, JSONObject objtuyendungquatrinhcongtac, JSONArray arrquatrinhcongtac) {
        SyncResponse syncresponse;
        if (arrquatrinhcongtac != null) {
            for (int i = 0; i < arrquatrinhcongtac.length(); i++) {
                JSONObject quatrinhcongtac = arrquatrinhcongtac.getJSONObject(i);
                quatrinhcongtac.put("NhanSu_Id", nhansu_id);
                syncresponse = getstoreProcedure(em, _quatrinhcongtacs, madonvi, quatrinhcongtac.toString());
                if (syncresponse.getErr_code() == 1)
                    return new SyncResponse(String.format("Cập nhật thông tin quá trình công tác không thành công: %s!", syncresponse.getMessage()), 1);
            }
            objtuyendungquatrinhcongtac.remove("DS_QUATRINH_CONGTAC");
        }
        if (!objtuyendungquatrinhcongtac.isEmpty()) {
            objtuyendungquatrinhcongtac.put("NhanSu_Id", nhansu_id);
            if (!objtuyendungquatrinhcongtac.getString("ViTriTuyenDung").isEmpty() && !validateCategory(dmvitrituyendung, objtuyendungquatrinhcongtac.getString("ViTriTuyenDung")))
                return new SyncResponse("Mã vị trí tuyển dụng không đúng với danh mục!", 1);
            syncresponse = getstoreProcedure(em, _tuyendungquatrinhcongtac, madonvi, objtuyendungquatrinhcongtac.toString());
            if (syncresponse.getErr_code() == 1)
                return new SyncResponse(String.format("Cập nhật thông tin tuyển dụng, quá trình công tác không thành công: %s!", syncresponse.getMessage()), 1);
        }
        return new SyncResponse(0);
    }

    public SyncResponse syncLuongphucapchucvu(EntityManager em, String madonvi, int nhansu_id, JSONObject objluongphucapchucvu, JSONArray arrquatrinhphucap, JSONArray arrquatrinhluong) {
        SyncResponse syncresponse;
        if (arrquatrinhphucap != null) {
            for (int i = 0; i < arrquatrinhphucap.length(); i++) {
                JSONObject quatrinhphucap = arrquatrinhphucap.getJSONObject(i);
                quatrinhphucap.put("NhanSu_Id", nhansu_id);
                if (!quatrinhphucap.getString("LoaiPhuCap").isEmpty() && !validateCategory(dmloaiphucap, quatrinhphucap.getString("LoaiPhuCap")))
                    return new SyncResponse("Mã loại phụ cấp không đúng với danh mục!", 1);
                if (!quatrinhphucap.getString("HinhThucHuong").isEmpty() && !validateCategory(dmhinhthuchuongphucap, quatrinhphucap.getString("HinhThucHuong")))
                    return new SyncResponse("Mã hình thức hưởng phụ cấp không đúng với danh mục!", 1);
                syncresponse = getstoreProcedure(em, _phucaps, madonvi, quatrinhphucap.toString());
                if (syncresponse.getErr_code() == 1)
                    return new SyncResponse(String.format("Cập nhật thông tin quá trình lương không thành công: %s!", syncresponse.getMessage()), 1);
            }
            objluongphucapchucvu.remove("DS_QUATRINH_PHUCAP");
        }
        if (arrquatrinhluong != null) {
            for (int i = 0; i < arrquatrinhluong.length(); i++) {
                JSONObject quatrinhluong = arrquatrinhluong.getJSONObject(i);
                quatrinhluong.put("NhanSu_Id", nhansu_id);
                if (!quatrinhluong.getString("Ngach").isEmpty() && !validateCategory(dmmangach_chucdanh, quatrinhluong.getString("Ngach")))
                    return new SyncResponse("Mã ngạch chức danh không đúng với danh mục!", 1);
                if (!quatrinhluong.getString("BacLuong").isEmpty() && !validateCategory(dmbacluong, quatrinhluong.getString("BacLuong")))
                    return new SyncResponse("Mã bậc lương không đúng với danh mục!", 1);
                syncresponse = getstoreProcedure(em, _luongs, madonvi, quatrinhluong.toString());
                if (syncresponse.getErr_code() == 1)
                    return new SyncResponse(String.format("Cập nhật thông tin quá trình phụ cấp không thành công: %s!", syncresponse.getMessage()), 1);
            }
            objluongphucapchucvu.remove("DS_QUATRINH_LUONG");
        }
        if (!objluongphucapchucvu.isEmpty()) {
            objluongphucapchucvu.put("NhanSu_Id", nhansu_id);
            if (!objluongphucapchucvu.getString("MaNgachChucDanh").isEmpty() && !validateCategory(dmmangach_chucdanh, objluongphucapchucvu.getString("MaNgachChucDanh")))
                return new SyncResponse("Mã ngạch chức danh không đúng với danh mục!", 1);
            if (!objluongphucapchucvu.getString("BacLuong").isEmpty() && !validateCategory(dmbacluong, objluongphucapchucvu.getString("BacLuong")))
                return new SyncResponse("Mã bậc lương không đúng với danh mục!", 1);

            if (!objluongphucapchucvu.getString("ChucVu").isEmpty() && !validateCategory(dmchucvu_chucdanhkn, objluongphucapchucvu.getString("ChucVu")))
                return new SyncResponse("Mã chức vụ không đúng với danh mục!", 1);
            if (!objluongphucapchucvu.getString("ChucVuChucDanhKiemNhiem").isEmpty() && !validateCategory(dmchucvu_chucdanhkn, objluongphucapchucvu.getString("ChucVuChucDanhKiemNhiem")))
                return new SyncResponse("Mã chức danh kiêm nhiệm không đúng với danh mục!", 1);
            syncresponse = getstoreProcedure(em, _luongphucap, madonvi, objluongphucapchucvu.toString());
            if (syncresponse.getErr_code() == 1)
                return new SyncResponse(String.format("Cập nhật thông tin lương, phụ cấp, chức vụ không thành công: %s!", syncresponse.getMessage()), 1);
        }
        return new SyncResponse(0);
    }

    public SyncResponse syncTrinhdodaotaoboiduong(EntityManager em, String madonvi, int nhansu_id, JSONObject objtrinhdodaotaoboiduong, JSONArray arrtinhoc, JSONArray jarrngoaingu, JSONArray arrquatrinhdaotaoboiduong) {
        SyncResponse syncresponse;
        if (arrtinhoc != null) {
            for (int i = 0; i < arrtinhoc.length(); i++) {
                JSONObject tinhoc = arrtinhoc.getJSONObject(i);
                tinhoc.put("NhanSu_Id", nhansu_id);
                if (!tinhoc.getString("TrinhDo").isEmpty() && !validateCategory(dmtrinhdotinhoc, tinhoc.getString("TrinhDo")))
                    return new SyncResponse("Mã trình độ tin học không đúng với danh mục!", 1);
                syncresponse = getstoreProcedure(em, _tinhocs, madonvi, tinhoc.toString());
                if (syncresponse.getErr_code() == 1)
                    return new SyncResponse(String.format("Cập nhật danh sách trình độ tin học không thành công: %s!", syncresponse.getMessage()), 1);
            }
            objtrinhdodaotaoboiduong.remove("DS_TINHOC");
        }

        if (jarrngoaingu != null) {
            for (int i = 0; i < jarrngoaingu.length(); i++) {
                JSONObject ngoaingu = jarrngoaingu.getJSONObject(i);
                ngoaingu.put("NhanSu_Id", nhansu_id);
                if (!ngoaingu.getString("MaNgoaiNgu").isEmpty() && !validateCategory(dmngoaingu, ngoaingu.getString("MaNgoaiNgu")))
                    return new SyncResponse("Mã ngoại ngữ không đúng với danh mục!", 1);
                if (!ngoaingu.getString("TrinhDo").isEmpty() && !validateCategory(dmtrinhdongoaingu, ngoaingu.getString("TrinhDo")))
                    return new SyncResponse("Mã trình độ ngoại ngữ không đúng với danh mục!", 1);
                syncresponse = getstoreProcedure(em, _ngoaingus, madonvi, ngoaingu.toString());
                if (syncresponse.getErr_code() == 1)
                    return new SyncResponse(String.format("Cập nhật danh sách trình độ ngoại ngữ không thành công: %s!", syncresponse.getMessage()), 1);
            }
            objtrinhdodaotaoboiduong.remove("DS_NGOAINGU");
        }
        if (arrquatrinhdaotaoboiduong != null) {
            for (int i = 0; i < arrquatrinhdaotaoboiduong.length(); i++) {
                JSONObject quatrinhdaotaoboiduong = arrquatrinhdaotaoboiduong.getJSONObject(i);
                quatrinhdaotaoboiduong.put("NhanSu_Id", nhansu_id);
                if (!quatrinhdaotaoboiduong.getString("ChuyenNganhDaoTao").isEmpty() && !validateCategory(dmchuyennganhdaotao, quatrinhdaotaoboiduong.getString("ChuyenNganhDaoTao")))
                    return new SyncResponse("Mã chuyên ngành đào tạo không đúng với danh mục!", 1);
                if (!quatrinhdaotaoboiduong.getString("TrinhDoDaoTao").isEmpty() && !validateCategory(dmtrinhdochuyenmondaotao, quatrinhdaotaoboiduong.getString("TrinhDoDaoTao")))
                    return new SyncResponse("Mã  trình độ đào tạo không đúng với danh mục!", 1);
                if (!quatrinhdaotaoboiduong.getString("XepLoaiTotNghiep").isEmpty() && !validateCategory(dmxeplaoitotnghiep, quatrinhdaotaoboiduong.getString("XepLoaiTotNghiep")))
                    return new SyncResponse("Mã xếp loại tốt nghiệp không đúng với danh mục!", 1);
                if (!quatrinhdaotaoboiduong.getString("NuocDaoTao").isEmpty() && !validateCategory(dmnuocdaotao, quatrinhdaotaoboiduong.getString("NuocDaoTao")))
                    return new SyncResponse("Mã nước đào tạo không đúng với danh mục!", 1);
                syncresponse = getstoreProcedure(em, _daotaoboiduongs, madonvi, quatrinhdaotaoboiduong.toString());
                if (syncresponse.getErr_code() == 1)
                    return new SyncResponse(String.format("Cập nhật quá trình đào tạo, bồi dưỡng không thành công: %s!", syncresponse.getMessage()), 1);
            }
            objtrinhdodaotaoboiduong.remove("DS_QUATRINH_DAOTAO_BOIDUONG");
        }
        if (!objtrinhdodaotaoboiduong.isEmpty()) {
            objtrinhdodaotaoboiduong.put("NhanSu_Id", nhansu_id);
            if (!objtrinhdodaotaoboiduong.getString("HocVanPhoThong").isEmpty() && !validateCategory(dmgiaoducphothong, objtrinhdodaotaoboiduong.getString("HocVanPhoThong")))
                return new SyncResponse("Mã học vấn phổ thông không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("TrinhDoChuyenMon").isEmpty() && !validateCategory(dmtrinhdochuyenmondaotao, objtrinhdodaotaoboiduong.getString("TrinhDoChuyenMon")))
                return new SyncResponse("Mã trình độ chuyên môn không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("TrinhDoLyLuanChinhTri").isEmpty() && !validateCategory(dmlyluanchinhtri, objtrinhdodaotaoboiduong.getString("TrinhDoLyLuanChinhTri")))
                return new SyncResponse("Mã trình độ lý luận chính trị không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("TrinhDoQuanLyNhaNuoc").isEmpty() && !validateCategory(dmquanlynhanuoc, objtrinhdodaotaoboiduong.getString("TrinhDoQuanLyNhaNuoc")))
                return new SyncResponse("Mã trình độ quản lý nhà nước không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("BoiDuongQuocPhongAnNinh").isEmpty() && !validateCategory(dmquocphonganninh, objtrinhdodaotaoboiduong.getString("BoiDuongQuocPhongAnNinh")))
                return new SyncResponse("Bồi dưỡng quốc phòng an ninh không đúng với quy định (0 hoặc 1)!", 1);
            if (!objtrinhdodaotaoboiduong.getString("MaChucDanhKhoaHoc").isEmpty() && !validateCategory(dmchucdanhkhoahoc, objtrinhdodaotaoboiduong.getString("MaChucDanhKhoaHoc")))
                return new SyncResponse("Mã chức danh khoa học không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("MaHocVi").isEmpty() && !validateCategory(dmhocvi, objtrinhdodaotaoboiduong.getString("MaHocVi")))
                return new SyncResponse("Mã học vị không đúng với danh mục!", 1);
            if (!objtrinhdodaotaoboiduong.getString("TiengDanTocThieuSo").isEmpty() && !validateCategory(dmtiengdantoc, objtrinhdodaotaoboiduong.getString("TiengDanTocThieuSo")))
                return new SyncResponse("Mã tiếng dân tộc không đúng với danh mục!", 1);
            syncresponse = getstoreProcedure(em, _trinhdodaotaoboiduong, madonvi, objtrinhdodaotaoboiduong.toString());
            if (syncresponse.getErr_code() == 1)
                return new SyncResponse(String.format("Cập nhật danh sách trình độ, đào tạo, bồi dưỡng không thành công: %s!", syncresponse.getMessage()), 1);
        }
        return new SyncResponse(0);
    }

    public SyncResponse syncThongtinkhac(EntityManager em, String madonvi, int nhansu_id, JSONObject objthongtinkhac) {
        SyncResponse syncresponse;
        objthongtinkhac.put("NhanSu_Id", nhansu_id);
        if (!objthongtinkhac.getString("ChucVuDang").isEmpty() && !validateCategory(dmchucdang, objthongtinkhac.getString("ChucVuDang")))
            return new SyncResponse("Mã chức vụ đảng không đúng với danh mục!", 1);
        syncresponse = getstoreProcedure(em, _thongtinkhac, madonvi, objthongtinkhac.toString());
        if (syncresponse.getErr_code() == 1)
            return new SyncResponse(String.format("Cập nhật thông tin khác không thành công: %s!", syncresponse.getMessage()), 1);
        return new SyncResponse(0);
    }

    public SyncResponse syncKetquadanhgaixeploai(EntityManager em, String madonvi, int nhansu_id, JSONArray arrketquadanhgia) {
        SyncResponse syncresponse;
        for (int i = 0; i < arrketquadanhgia.length(); i++) {
            JSONObject ketquadanhgia = arrketquadanhgia.getJSONObject(i);
            ketquadanhgia.put("NhanSu_Id", nhansu_id);
            if (!ketquadanhgia.getString("KetQuaDanhGia").isEmpty() && !validateCategory(dmkettquadanhgia, ketquadanhgia.getString("KetQuaDanhGia")))
                return new SyncResponse("Mã kết quả đánh giá không đúng với danh mục!", 1);
            syncresponse = getstoreProcedure(em, _danhgiaphanloais, madonvi, ketquadanhgia.toString());
            if (syncresponse.getErr_code() == 1)
                return new SyncResponse(String.format("Cập nhật danh sách kết quả, đánh giá, xếp loại không thành công: %s!", syncresponse.getMessage()), 1);
        }
        return new SyncResponse(0);
    }

    //xóa dữ liệu
    public SyncResponse syncXoadulieu(EntityManager em, String madonvi, String objdel) {
        try {
            SyncResponse syncresponse = getstoreProcedure(em, _xoahosonhansu, madonvi, objdel);
            if (syncresponse.getErr_code() == 1)
                return new SyncResponse(String.format("Xóa dữ liệu hồ sơ nhân sự không thành công: %s!", syncresponse.getMessage()), 1);
            return new SyncResponse(0);
        } catch (Exception ex) {
            return new SyncResponse(String.format("Xóa dữ liệu hồ sơ nhân sự không thành công: %s!", ex.getMessage()), 1);
        }
    }

    //kiểm tra số hiệu cbccvc_bndp
    public SyncResponse syncSohieuCbccvcBndp(EntityManager em, String madonvi, String sohieucbccvc_bndp) {
        try {
            SyncResponse syncresponse = getstoreProcedure(em, _sohieucbccvc_bndp, madonvi, sohieucbccvc_bndp);
            if (syncresponse.getErr_code() == 1)
                return new SyncResponse(String.format("Kiểm tra hồ sơ nhân sự không thành công: %s!", syncresponse.getMessage()), 1);
            return syncresponse;
        } catch (Exception ex) {
            return new SyncResponse("Kiểm tra Số hiệu cán bộ, công chức, viên chức Bộ ngành địa phương không thành công: " + ex.getMessage(), 1);
        }
    }

    //kiểm tra danh mục
    public boolean validateCategory(List<String> list, String search) {
        Iterable<String> result = Iterables.filter(list, Predicates.containsPattern(search));
        List<String> stringList = Lists.newArrayList(result.iterator());
        if (stringList.size() > 0)
            return true;
        else return false;
    }

    // xử lý json trả về
    public SyncResponse getstoreProcedure(EntityManager em, String storename, String madonvi, String json) {
        try {
            madonvi = "";
            String s = syncMsgService.callStoreProcedure(em, storename, madonvi, json);
            JSONObject retjson = new JSONObject(s);
            String mess = retjson.getString("MSG_TEXT");
            int err = retjson.getInt("MSG_CODE");
            String val = retjson.getString("VAL");
            return new SyncResponse(mess, err == 1 ? 0 : 1, val);
        } catch (Exception ex) {
            return new SyncResponse(ex.getMessage(), 1);
        }
    }
}

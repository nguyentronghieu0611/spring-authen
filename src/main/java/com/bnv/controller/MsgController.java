package com.bnv.controller;

import java.util.Map;


import com.bnv.model.Response;
import com.bnv.repository.AdmUserRepository;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ApiSyncMsg/SyncMsg")
public class MsgController {
    @Autowired
    AdmUserRepository admUserRepository;

    @Autowired
    SyncMsg syncMsg;

    private static String _header = "pkg_parse_json.parse_header_json";
    private static String _ns_thongtinchung = "pkg_parse_json.parse_ns_thongtinchung_json";
    private static String _ns_tuyendungquatrinhcongtac = "pkg_parse_json.parse_ns_tuyendung_qtct_json";
    private static String _ns_quatrinhcongtac = "pkg_parse_json.parse_ns_quatrinhcongtac_json";
    private static String _ns_luongphucap = "pkg_parse_json.parse_ns_luongphucap_json";
    private static String _luong = "pkg_parse_json.parse_ns_luong_json";
    private static String _phucap = "pkg_parse_json.parse_ns_phucap_json";
    private static String _ns_daotaoboiduong = "pkg_parse_json.parse_ns_daotaoboiduong_json";
    private static String _daotaoboiduong = "pkg_parse_json.parse_daotaoboiduong_json";
    private static String _tinhoc = "pkg_parse_json.parse_tinhoc_json";
    private static String _ngoaingu = "pkg_parse_json.parse_ngoaingu_json";
    private static String _ns_thongtinkhac = "pkg_parse_json.parse_ns_thongtinkhac_json";
    private static String _danhgiaphanloai = "pkg_parse_json.parse_danhgiaphanloai_json";
    private static String _xoahosonhansu = "pkg_parse_json.parse_ns_xoahosonhansu_json";

    // Thông điệp thêm mới hồ sơ CBCCVC vào hệ thống
    @RequestMapping("/ServiceM0001")
    public ResponseEntity<?> ServiceM0001_01(@RequestBody Map<String, String> body) {
        try {

            Response response = null;
            String nhansu_id = null;
            String madonvi = body.get("org_Code");
            String strJson = body.get("jsonContent");
            byte[] decoded = Base64.decodeBase64(strJson);
            String strJsonDecode = new String(decoded, "UTF-8");

            JSONObject jsonObj = new JSONObject(strJsonDecode);
            JSONObject data = jsonObj.getJSONObject("Data");
            JSONObject header = data.getJSONObject("Header");

            // thêm mới vào bảng header
            response = syncMsg.getstoreProcedure(_header, madonvi, header.toString());
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));

            JSONObject Body = data.getJSONObject("Body");
            JSONObject hoso_cbccvc = Body.getJSONObject("HOSO_CBCCVC");

            JSONObject thongtinchung = hoso_cbccvc.getJSONObject("THONGTINCHUNG");

            //kiểm tra và tạo mã số hiệu cán bộ bộ ngành địa phương
            String soHieucbccvc_bndp = thongtinchung.getString("SoHieuCBCCVC_BNDP");
            if (soHieucbccvc_bndp == null || soHieucbccvc_bndp.isEmpty())
                return ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: Không tìm thấy số hiệu CBCCVC ", 1));

            JSONObject sohieucbccvc_bndp = new JSONObject();
            String sohieubndp_bnv = madonvi + "_" + soHieucbccvc_bndp;
            sohieucbccvc_bndp.put("SoHieuCBCCVC_BNDP", sohieubndp_bnv);

            // thêm mới thông tin chung vào bảng nhân sự
            response = syncMsg.setThongtinchung(thongtinchung, madonvi, sohieubndp_bnv);
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            else
                nhansu_id = response.getValue();


            JSONObject tuyendungquatrinhcongtac = hoso_cbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC");
            response = syncMsg.setTuyendungquatrinhcongtac(tuyendungquatrinhcongtac, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray quatrinhcongtacs = tuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC");
            response = syncMsg.setQuatrinhcongtacs(quatrinhcongtacs, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONObject luongphucapchucvu = hoso_cbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU");
            response = syncMsg.setLuongphucapchucvu(luongphucapchucvu, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray quatrinhphucaps = luongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP");
            response = syncMsg.setQuatrinhphucap(quatrinhphucaps, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray quatrinhluongs = luongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG");
            response = syncMsg.setQuatrinhluong(quatrinhluongs, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONObject daotaoboiduong = hoso_cbccvc.getJSONObject("DAOTAO_BOIDUONG");
            response = syncMsg.setDaotaoboiduong(daotaoboiduong, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray tinhocs = daotaoboiduong.getJSONArray("DS_TINHOC");
            response = syncMsg.setTinhocs(tinhocs, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray ngoaingus = daotaoboiduong.getJSONArray("DS_NGOAINGU");
            response = syncMsg.setNgoaingus(ngoaingus, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray quatrinhdaotaobuoiduongs = daotaoboiduong.getJSONArray("DS_QUATRINH_DAOTAO_BOIDUONG");
            response = syncMsg.setQuatrinhdaotaoboiduongs(quatrinhdaotaobuoiduongs, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONObject thongtinkhac = hoso_cbccvc.getJSONObject("THONGTIN_KHAC");
            response = syncMsg.setThongtinkhac(thongtinkhac, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray ketquadanhgiaphanloais = hoso_cbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI");
            response = syncMsg.setKetquadanhgiaphanloais(ketquadanhgiaphanloais, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return syncMsg.delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            System.out.println("--------------------------------------------------------import hosonhansu");
            System.out.println("Thêm mới hồ sơ nhân sư thành công " + sohieubndp_bnv);
            return ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sư thành công", 0));

        } catch (Exception ex) {
            return ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: " + ex.getMessage(), 1));
        }
    }
}

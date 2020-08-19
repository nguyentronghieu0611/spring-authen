package com.bnv.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bnv.model.Response;
import com.bnv.repository.AdmUserRepository;
import com.bnv.service.SyncMsgService;
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
    SyncMsgService syncMsg;

    private static String _header = "pkg_parse_json.parse_header_json";

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

            if (!checkActionType(header))
                return ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: Thẻ Action_Type đang không có hoặc rỗng hoặc không có trong danh sách  ADD, DEL, EDIT, VIEW", 1));

            // thêm mới vào bảng header
            response = syncMsg.getstoreProcedure(_header, madonvi, header.toString());
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));


            String action_type = header.getString("Action_Type");


            JSONObject Body = data.getJSONObject("Body");
            JSONObject hoso_cbccvc = Body.getJSONObject("HOSO_CBCCVC");
            String soHieucbccvc_bndp = hoso_cbccvc.getString("SoHieuCBCCVC_BNDP");

            JSONObject thongtinchung = hoso_cbccvc.getJSONObject("THONGTINCHUNG");
            //kiểm tra và tạo mã số hiệu cán bộ bộ ngành địa phương
            if (soHieucbccvc_bndp == null || soHieucbccvc_bndp.isEmpty())
                return ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: Không tìm thấy số hiệu CBCCVC ", 1));

            JSONObject jsonSohieucbccvc_bndp = new JSONObject();
            String sohieubndp_bnv = madonvi + "_" + soHieucbccvc_bndp;
            jsonSohieucbccvc_bndp.put("SoHieuCBCCVC_BNDP", sohieubndp_bnv);
            int hosonhansu_id = checkSoHieuCbccvcBndp(jsonSohieucbccvc_bndp);
            if (action_type.equals("ADD") && hosonhansu_id > 0)
                return ResponseEntity.ok(new Response(String.format("Thêm mới thông tin chung hồ sơ không thành công: Hồ sơ có số hiệu CBCCVC %s đã tồn tại!", soHieucbccvc_bndp), 1));
            else if ((action_type.equals("EDIT") || action_type.equals("DEL")) && hosonhansu_id <= 0)
                return ResponseEntity.ok(new Response(String.format("Cập nhật thông tin chung hồ sơ không thành công: Hồ sơ có số hiệu CBCCVC %s không tồn tại!", soHieucbccvc_bndp), 1));

            // thêm mới thông tin chung vào bảng nhân sự
            thongtinchung.put("NhanSu_Id", hosonhansu_id);
            response = syncMsg.setThongtinchung(thongtinchung, madonvi, sohieubndp_bnv);
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            else
                nhansu_id = response.getValue();

            if (!hoso_cbccvc.isNull("TUYENDUNG_QT_CONGTAC") && !hoso_cbccvc.getString("TUYENDUNG_QT_CONGTAC").isEmpty()) {
                JSONObject tuyendungquatrinhcongtac = hoso_cbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC");
                response = syncMsg.setTuyendungquatrinhcongtac(tuyendungquatrinhcongtac, madonvi, nhansu_id);
                if (response.getErr_code() == 1)
                    return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());

                if (!hoso_cbccvc.isNull("DS_QUATRINH_CONGTAC") && !hoso_cbccvc.getString("DS_QUATRINH_CONGTAC").isEmpty()) {
                    JSONArray quatrinhcongtacs = tuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC");
                    response = syncMsg.setQuatrinhcongtacs(quatrinhcongtacs, madonvi, nhansu_id);
                    if (response.getErr_code() == 1)
                        return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());
                }
            }

            if (!hoso_cbccvc.isNull("LUONG_PHUCAP_CHUCVU") && !hoso_cbccvc.getString("LUONG_PHUCAP_CHUCVU").isEmpty()) {
                JSONObject luongphucapchucvu = hoso_cbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU");
                response = syncMsg.setLuongphucapchucvu(luongphucapchucvu, madonvi, nhansu_id);
                if (response.getErr_code() == 1)
                    return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());

                if (!hoso_cbccvc.isNull("DS_QUATRINH_PHUCAP") && !hoso_cbccvc.getString("DS_QUATRINH_PHUCAP").isEmpty()) {
                    JSONArray quatrinhphucaps = luongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP");
                    response = syncMsg.setQuatrinhphucap(quatrinhphucaps, madonvi, nhansu_id);
                    if (response.getErr_code() == 1)
                        return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());
                }

                if (!hoso_cbccvc.isNull("DS_QUATRINH_LUONG") && !hoso_cbccvc.getString("DS_QUATRINH_LUONG").isEmpty()) {
                    JSONArray quatrinhluongs = luongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG");
                    response = syncMsg.setQuatrinhluong(quatrinhluongs, madonvi, nhansu_id);
                    if (response.getErr_code() == 1)
                        return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());
                }
            }

            if (!hoso_cbccvc.isNull("TRINHDO_DAOTAO_BOIDUONG") && !hoso_cbccvc.getString("TRINHDO_DAOTAO_BOIDUONG").isEmpty()) {
                JSONObject daotaoboiduong = hoso_cbccvc.getJSONObject("TRINHDO_DAOTAO_BOIDUONG");
                response = syncMsg.setDaotaoboiduong(daotaoboiduong, madonvi, nhansu_id);
                if (response.getErr_code() == 1)
                    return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());

                if (!hoso_cbccvc.isNull("DS_TINHOC") && !hoso_cbccvc.getString("DS_TINHOC").isEmpty()) {
                    JSONArray tinhocs = daotaoboiduong.getJSONArray("DS_TINHOC");
                    response = syncMsg.setTinhocs(tinhocs, madonvi, nhansu_id);
                    if (response.getErr_code() == 1)
                        return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());
                }
                if (!hoso_cbccvc.isNull("DS_NGOAINGU") && !hoso_cbccvc.getString("DS_NGOAINGU").isEmpty()) {
                    JSONArray ngoaingus = daotaoboiduong.getJSONArray("DS_NGOAINGU");
                    response = syncMsg.setNgoaingus(ngoaingus, madonvi, nhansu_id);
                    if (response.getErr_code() == 1)
                        return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());
                }
                if (!hoso_cbccvc.isNull("DS_QUATRINH_DAOTAO_BOIDUONG") && !hoso_cbccvc.getString("DS_QUATRINH_DAOTAO_BOIDUONG").isEmpty()) {
                    JSONArray quatrinhdaotaobuoiduongs = daotaoboiduong.getJSONArray("DS_QUATRINH_DAOTAO_BOIDUONG");
                    response = syncMsg.setQuatrinhdaotaoboiduongs(quatrinhdaotaobuoiduongs, madonvi, nhansu_id);
                    if (response.getErr_code() == 1)
                        return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());
                }
            }

            if (!hoso_cbccvc.isNull("THONGTIN_KHAC") && !hoso_cbccvc.getString("THONGTIN_KHAC").isEmpty()) {
                JSONObject thongtinkhac = hoso_cbccvc.getJSONObject("THONGTIN_KHAC");
                response = syncMsg.setThongtinkhac(thongtinkhac, madonvi, nhansu_id);
                if (response.getErr_code() == 1)
                    return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());
            }

            if (!hoso_cbccvc.isNull("DS_KETQUA_DANHGIA_PHANLOAI") && !hoso_cbccvc.getString("DS_KETQUA_DANHGIA_PHANLOAI").isEmpty()) {
                JSONArray ketquadanhgiaphanloais = hoso_cbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI");
                response = syncMsg.setKetquadanhgiaphanloais(ketquadanhgiaphanloais, madonvi, nhansu_id);
                if (response.getErr_code() == 1)
                    return syncMsg.delHosonhansu(response, madonvi, jsonSohieucbccvc_bndp.toString());
            }


            System.out.println("--------------------------------------------------------import hosonhansu");
            System.out.println("Thêm mới hồ sơ nhân sư thành công " + sohieubndp_bnv);
            return ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sư thành công", 0));

        } catch (Exception ex) {
            return ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: " + ex.getMessage(), 1));
        }
    }

    //check hành động trọng json
    private boolean checkActionType(JSONObject header) {
        boolean status = true;
        List<String> listAction_Type = Arrays.asList(new String[]{"ADD", "EDIT", "DEL"});
        if (header.isNull("Action_Type") || header.getString("Action_Type").isEmpty())
            status = false;
        else if (!listAction_Type.contains(header.getString("Action_Type")))
            status = false;
        return status;
    }

    //check SoHieuCBCCVC_BNDP
    // -1: không tìm thấy thẻ liệu trong thẻ, 0: không tìm thấy tồn tại, khác -1,0 : là số id bản ghi tìm được ,:
    private int checkSoHieuCbccvcBndp(JSONObject hoso_cbccvc) {
        Response response = null;
        int status = 0;
        if (hoso_cbccvc.isNull("SoHieuCBCCVC_BNDP") || hoso_cbccvc.getString("SoHieuCBCCVC_BNDP").isEmpty())
            status = -1;
        else {
            response = syncMsg.selSohieuCbccvc_Bndp("", hoso_cbccvc.toString());
            if (response.getErr_code() == 1)
                status = -1;
            else if (response.getValue().equals(""))
                status = 0;
            else
                status = Integer.parseInt(response.getValue());
        }
        return status;
    }
}

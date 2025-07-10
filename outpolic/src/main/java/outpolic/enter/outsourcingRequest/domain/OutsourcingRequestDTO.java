package outpolic.enter.outsourcingRequest.domain;



// '외주 신청' 폼에서 데이터를 받기 위한 DTO
public class OutsourcingRequestDTO {

private String ocd_cd;
private String ocd_req_type;
private String mbr_cd;
private String ent_cd;
private String cl_cd;
private String ocd_ttl;
private String ocd_expln;
private String ocd_frctn_cmdty;
private String ocd_dlvgds_mthd;
private String ocd_dedline;
private Long ocd_amt;

// ▼▼▼ Getter와 Setter 추가 ▼▼▼

public String getOcd_cd() {
    return ocd_cd;
}
public void setOcd_cd(String ocd_cd) {
    this.ocd_cd = ocd_cd;
}
public String getOcd_req_type() {
    return ocd_req_type;
}
public void setOcd_req_type(String ocd_req_type) {
    this.ocd_req_type = ocd_req_type;
}
public String getMbr_cd() {
    return mbr_cd;
}
public void setMbr_cd(String mbr_cd) {
    this.mbr_cd = mbr_cd;
}
public String getEnt_cd() {
    return ent_cd;
}
public void setEnt_cd(String ent_cd) {
    this.ent_cd = ent_cd;
}
public String getCl_cd() {
    return cl_cd;
}
public void setCl_cd(String cl_cd) {
    this.cl_cd = cl_cd;
}
public String getOcd_ttl() {
    return ocd_ttl;
}
public void setOcd_ttl(String ocd_ttl) {
    this.ocd_ttl = ocd_ttl;
}
public String getOcd_expln() {
    return ocd_expln;
}
public void setOcd_expln(String ocd_expln) {
    this.ocd_expln = ocd_expln;
}
public String getOcd_frctn_cmdty() {
    return ocd_frctn_cmdty;
}
public void setOcd_frctn_cmdty(String ocd_frctn_cmdty) {
    this.ocd_frctn_cmdty = ocd_frctn_cmdty;
}
public String getOcd_dlvgds_mthd() {
    return ocd_dlvgds_mthd;
}
public void setOcd_dlvgds_mthd(String ocd_dlvgds_mthd) {
    this.ocd_dlvgds_mthd = ocd_dlvgds_mthd;
}
public String getOcd_dedline() {
    return ocd_dedline;
}
public void setOcd_dedline(String ocd_dedline) {
    this.ocd_dedline = ocd_dedline;
}
public Long getOcd_amt() {
    return ocd_amt;
}
public void setOcd_amt(Long ocd_amt) {
    this.ocd_amt = ocd_amt;
}
}
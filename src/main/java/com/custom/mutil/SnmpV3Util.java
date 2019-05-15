package com.custom.mutil;

import org.snmp4j.*;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpV3Util {

    private static final OID oidFswAlarm = new OID(new int[] { 1, 3, 6, 1, 4, 1, 1943, 1, 1, 0 });
    // SNMPV3认证协议
    private static final Integer SNMP_V3_AUTH_PROTOCOL_NO = 1;
    private static final Integer SNMP_V3_AUTH_PROTOCOL_MD5 = 2;
    private static final Integer SNMP_V3_AUTH_PROTOCOL_SHA = 3;
    // SNMPV3加密协议
    private static final Integer SNMP_V3_PRI_PROTOCOL_NO = 1;
    private static final Integer SNMP_V3_PRI_PROTOCOL_DES = 2;

    private Snmp snmp;
    private USM usm;
    private UserTarget target;
    private byte[] enginId;
    //
    private String serverIP;
    private String serverPort;
    private String securityName;// 用户名，该字段及以下字段仅V3协议有效
    private Integer authProtocol;// 认证协议1：NoAuthProtocol2：HMACMD5AuthProtocol;3：HMACSHAAuthProtocol
    private String authKey;// 认证秘钥
    private Integer privacyProtocol;// 加密协议 1：NoPrivProtocol 2：DESPrivProtocol
    private String privacyKey;// 加密秘钥

    public SnmpV3Util(String serverIP, String serverPort, String securityName, Integer authProtocol, String authKey, Integer privacyProtocol,
                      String privacyKey) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.securityName = securityName;
        this.authProtocol = authProtocol;
        this.authKey = authKey;
        this.privacyProtocol = privacyProtocol;
        this.privacyKey = privacyKey;
    }

    public void init() throws Exception {
        SNMP4JSettings.setExtensibilityEnabled(true);
        SecurityProtocols.getInstance().addDefaultProtocols();
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        enginId = MPv3.createLocalEngineID();
        usm = new USM(SecurityProtocols.getInstance(), new OctetString(enginId), 500);
        SecurityModels secModels = SecurityModels.getInstance();
        synchronized (secModels) {
            if (snmp.getUSM() == null) {
                secModels.addSecurityModel(usm);
            }
        }
        transport.listen();
        //
        target = new UserTarget();
        target.setVersion(SnmpConstants.version3);
        target.setAddress(new UdpAddress(serverIP + "/" + serverPort));
        target.setSecurityLevel(findSecurityLevel());
        target.setSecurityName(new OctetString(securityName));
        target.setTimeout(3000);
        target.setRetries(1);
    }

    public void logout() throws Exception {
        if (snmp != null) {
            snmp.close();
        }
    }

    public void sendAlarm(VariableBinding[] vbs) throws Exception {
        OID oid = new OID(oidFswAlarm);
        ScopedPDU pdu = new ScopedPDU();
        pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(System.currentTimeMillis() / 1000)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, oid));
        pdu.addAll(vbs);
        pdu.setType(PDU.NOTIFICATION);
        //
        sendPDU(pdu);
    }

    public void sendPDU(PDU pdu) throws Exception {
        if (snmp == null) {
            init();
        }
        UsmUser usmUser = findUsmUser();
        snmp.getUSM().addUser(new OctetString(securityName), new OctetString(enginId), usmUser);
        snmp.setLocalEngine(enginId, 500, 1);
        snmp.notify(pdu, target);
    }

    /**
     * 获取加密等级
     */
    private int findSecurityLevel() throws Exception {
        if (SNMP_V3_AUTH_PROTOCOL_NO.equals(authProtocol)) {
            return SecurityLevel.NOAUTH_NOPRIV;
        } else if (SNMP_V3_AUTH_PROTOCOL_MD5.equals(authProtocol)) {
            if (SNMP_V3_PRI_PROTOCOL_NO.equals(privacyProtocol)) {
                return SecurityLevel.AUTH_NOPRIV;
            } else if (SNMP_V3_PRI_PROTOCOL_DES.equals(privacyProtocol)) {
                return SecurityLevel.AUTH_PRIV;
            }
            throw new Exception("alarmReportManage privacyProtocol error:" + privacyProtocol);
        } else if (SNMP_V3_AUTH_PROTOCOL_SHA.equals(authProtocol)) {
            if (SNMP_V3_PRI_PROTOCOL_NO.equals(privacyProtocol)) {
                return SecurityLevel.AUTH_NOPRIV;
            } else if (SNMP_V3_PRI_PROTOCOL_DES.equals(privacyProtocol)) {
                return SecurityLevel.AUTH_PRIV;
            }
            throw new Exception("alarmReportManage privacyProtocol error:" + privacyProtocol);
        }
        throw new Exception("alarmReportManage authProtocol error:" + authProtocol);
    }

    /**
     * 获取加密用户
     */
    private UsmUser findUsmUser() throws Exception {
        if (SNMP_V3_AUTH_PROTOCOL_NO.equals(authProtocol)) {
            return new UsmUser(new OctetString(securityName), null, null, null, null);
        } else if (SNMP_V3_AUTH_PROTOCOL_MD5.equals(authProtocol)) {
            if (SNMP_V3_PRI_PROTOCOL_NO.equals(privacyProtocol)) {
                return new UsmUser(new OctetString(securityName), AuthMD5.ID, new OctetString(authKey), null,
                        null);
            } else if (SNMP_V3_PRI_PROTOCOL_DES.equals(privacyProtocol)) {
                return new UsmUser(new OctetString(securityName), AuthMD5.ID, new OctetString(authKey),
                        PrivDES.ID, new OctetString(privacyKey));
            }
            throw new Exception("alarmReportManage privacyProtocol error:" + privacyProtocol);
        } else if (SNMP_V3_AUTH_PROTOCOL_SHA.equals(authProtocol)) {
            if (SNMP_V3_PRI_PROTOCOL_NO.equals(privacyProtocol)) {
                return new UsmUser(new OctetString(securityName), AuthSHA.ID, new OctetString(authKey), null,
                        null);
            } else if (SNMP_V3_PRI_PROTOCOL_DES.equals(privacyProtocol)) {
                return new UsmUser(new OctetString(securityName), AuthSHA.ID, new OctetString(authKey),
                        PrivDES.ID, new OctetString(privacyKey));
            }
            throw new Exception("alarmReportManage privacyProtocol error:" + privacyProtocol);
        }
        throw new Exception("alarmReportManage authProtocol error:" + authProtocol);
    }
}

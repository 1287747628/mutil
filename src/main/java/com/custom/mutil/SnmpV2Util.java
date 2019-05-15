package com.custom.mutil;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpV2Util {

    private static final OID oidOmcHeartMsg = new OID(new int[] { 1, 3, 6, 1, 4, 1, 1943, 2, 2, 0 });

    private Snmp snmp = null;
    private Address targetAddress = null;
    private CommunityTarget target = null;
    private String serverIP = null;
    private String serverPort = null;

    public SnmpV2Util(String serverIP, String serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void init() throws Exception {
        // 设置管理进程的IP和端口
        String ip = serverIP;
        String port = serverPort;
        targetAddress = GenericAddress.parse("udp:" + ip + "/" + port);
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
        // 设置 target
        target = new CommunityTarget();
        target.setAddress(targetAddress);
        // 通信不成功时的重试次数
        target.setRetries(1);
        // 超时时间
        target.setTimeout(3000);
        // snmp版本
        target.setVersion(SnmpConstants.version2c);
    }

    public void logout() throws Exception {
        if (snmp != null) {
            snmp.close();
        }
    }

    public void sendAlarm(VariableBinding[] vbs) throws Exception {
        OID oid = new OID(oidOmcHeartMsg);
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new Integer32(0)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, oid));
        pdu.addAll(vbs);
        //
        sendPDU(pdu);
    }

    public void sendPDU(PDU pdu) throws Exception {
        if (snmp == null) {
            init();
        }
        snmp.notify(pdu, target);
    }
}

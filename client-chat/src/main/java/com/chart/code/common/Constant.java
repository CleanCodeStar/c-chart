package com.chart.code.common;

import cn.hutool.core.lang.Snowflake;

import java.awt.*;

/**
 * Constant
 *
 * @author CleanCode
 */
public class Constant {
    /**
     * 背景颜色
     */
    public final static Color BACKGROUND_COLOR = Color.WHITE;
    /**
     * 雪花算法
     */
    public final static Snowflake SNOWFLAKE = new Snowflake(0, 0);
    /**
     * 现调颜色
     */
    public final static Color CURRENT_COLOR = new Color(241, 241, 241);

    public final static String DIALOGUE_HTML = "<html lang='zh'>" +
            "<head>" +
            "    <meta charset='UTF-8'>" +
            "    <style>" +
            "        .main {" +
            "            display: flex;" +
            "            flex-flow: column;" +
            "        }" +
            "        .time {" +
            "            display: flex;" +
            "            flex-flow: row;" +
            "            text-align: center;" +
            "            margin-bottom: 10px;" +
            "            justify-content: center;" +
            "        }" +
            "        .time-value {" +
            "            display: flex;" +
            "            flex-flow: row;" +
            "            justify-content: center;" +
            "            background-color: #f0f0f0;" +
            "            padding: 8px;" +
            "        }" +
            "        .own {" +
            "            display: flex;" +
            "            flex-flow: row-reverse;" +
            "            margin-bottom: 10px;" +
            "        }" +
            "        .own-message {" +
            "            display: flex;" +
            "            flex-flow: column;" +
            "            align-items: center;" +
            "            max-width: 70%;" +
            "            background-color: #d2f3d1;" +
            "            padding: 10px;" +
            "            margin-right: 10px;" +
            "            border-radius: 10px;" +
            "            word-wrap: anywhere;" +
            "            white-space: pre-wrap;" +
            "        }" +
            "        .head {" +
            "            width: 40px;" +
            "            border-radius: 3px;" +
            "        }" +
            "        .friend {" +
            "            display: flex;" +
            "            flex-flow: row;" +
            "            margin-bottom: 10px;" +
            "        }" +
            "        .friend-message {" +
            "            display: flex;" +
            "            flex-flow: column;" +
            "            align-items: center;" +
            "            max-width: 70%;" +
            "            background-color: #bfdaf1;" +
            "            padding: 10px;" +
            "            margin-left: 10px;" +
            "            border-radius: 10px;" +
            "            word-wrap: anywhere;" +
            "            white-space: pre-wrap;" +
            "        }" +
            "        .file-message {" +
            "            display: flex;" +
            "            flex-flow: column;" +
            "            align-items: center;" +
            "            max-width: 70%;" +
            "            padding: 10px;" +
            "            margin-right: 10px;" +
            "            border-radius: 10px;" +
            "        }" +
            "        .file {" +
            "            display: flex;" +
            "            flex-flow: row;" +
            "            padding: 0 10px;" +
            "            justify-content: space-between;" +
            "            align-items: center;" +
            "            word-wrap: anywhere;" +
            "        }" +
            "        .file-info {" +
            "            display: flex;" +
            "            flex-flow: column;" +
            "            min-height: 60px;" +
            "        }" +
            "        .file-name {" +
            "            font-size: 16px;" +
            "            font-weight: bold;" +
            "            width: 100%;" +
            "            word-wrap: anywhere;" +
            "            white-space: pre-wrap;" +
            "        }" +
            "        .file-size {" +
            "            margin-top: 8px;" +
            "            font-size: 14px;" +
            "        }" +
            "        .file-suffix {" +
            "            margin-left: 30px;" +
            "            width: 40px;" +
            "            height: 40px;" +
            "        }" +
            "    </style>" +
            "<script type='text/javascript'>" +
            "    function ownMessage(head,message){" +
            "        let ownDiv = document.createElement('div');" +
            "        ownDiv.className = 'own';" +
            "        let headDiv = document.createElement('div');" +
            "        headDiv.className = 'head';" +
            "        let headImg = document.createElement('img');" +
            "        headImg.src = head;" +
            "        headImg.className = 'head';" +
            "        headDiv.appendChild(headImg);" +
            "        ownDiv.appendChild(headDiv);" +
            "        let ownMessageDiv = document.createElement('div');" +
            "        ownMessageDiv.className = 'own-message';" +
            "        ownMessageDiv.innerHTML = message;" +
            "        ownDiv.appendChild(ownMessageDiv);" +
            "        document.getElementById('chat').appendChild(ownDiv);" +
            "    }" +
            "    function friendMessage(head,message){" +
            "        let ownDiv = document.createElement('div');" +
            "        ownDiv.className = 'friend';" +
            "        let headDiv = document.createElement('div');" +
            "        headDiv.className = 'head';" +
            "        let headImg = document.createElement('img');" +
            "        headImg.src = head;" +
            "        headImg.className = 'head';" +
            "        headDiv.appendChild(headImg);" +
            "        ownDiv.appendChild(headDiv);" +
            "        let ownMessageDiv = document.createElement('div');" +
            "        ownMessageDiv.className = 'friend-message';" +
            "        ownMessageDiv.innerHTML = message;" +
            "        ownDiv.appendChild(ownMessageDiv);" +
            "        document.getElementById('chat').appendChild(ownDiv);" +
            "    }" +
            "    function ownFileMessage(head,fileName,fileSize,fileSuffix){" +
            "        let ownDiv = document.createElement('div');" +
            "        ownDiv.className = 'own';" +
            "        let headDiv = document.createElement('div');" +
            "        headDiv.className = 'head';" +
            "        let headImg = document.createElement('img');" +
            "        headImg.src = head;" +
            "        headImg.className = 'head';" +
            "        headDiv.appendChild(headImg);" +
            "        ownDiv.appendChild(headDiv);" +
            "        let ownMessageDiv = document.createElement('div');" +
            "        ownMessageDiv.className = 'own-message';" +
            "        let fileDiv = document.createElement('div');" +
            "        fileDiv.className = 'file';" +
            "        let fileInfoDiv = document.createElement('div');" +
            "        fileInfoDiv.className = 'file-info';" +
            "        let fileNameDiv = document.createElement('div');" +
            "        fileNameDiv.className = 'file-name';" +
            "        fileNameDiv.textContent = fileName;" +
            "        let fileSizeDiv = document.createElement('div');" +
            "        fileSizeDiv.className = 'file-size';" +
            "        fileSizeDiv.textContent = fileSize;" +
            "        fileInfoDiv.appendChild(fileNameDiv);" +
            "        fileInfoDiv.appendChild(fileSizeDiv);" +
            "        let fileSuffixDiv = document.createElement('img');" +
            "        fileSuffixDiv.className = 'file-suffix';" +
            "        fileSuffixDiv.src = fileSuffix;" +
            "        fileDiv.appendChild(fileInfoDiv);" +
            "        fileDiv.appendChild(fileSuffixDiv);" +
            "        ownMessageDiv.appendChild(fileDiv);" +
            "        ownDiv.appendChild(ownMessageDiv);" +
            "        document.getElementById('chat').appendChild(ownDiv);" +
            "    }" +
            "    function friendFileMessage(head,fileName,fileSize,fileSuffix){" +
            "        let ownDiv = document.createElement('div');" +
            "        ownDiv.className = 'friend';" +
            "        let headDiv = document.createElement('div');" +
            "        headDiv.className = 'head';" +
            "        let headImg = document.createElement('img');" +
            "        headImg.src = head;" +
            "        headImg.className = 'head';" +
            "        headDiv.appendChild(headImg);" +
            "        ownDiv.appendChild(headDiv);" +
            "        let ownMessageDiv = document.createElement('div');" +
            "        ownMessageDiv.className = 'friend-message';" +
            "        let fileDiv = document.createElement('div');" +
            "        fileDiv.className = 'file';" +
            "        let fileInfoDiv = document.createElement('div');" +
            "        fileInfoDiv.className = 'file-info';" +
            "        let fileNameDiv = document.createElement('div');" +
            "        fileNameDiv.className = 'file-name';" +
            "        fileNameDiv.textContent = fileName;" +
            "        let fileSizeDiv = document.createElement('div');" +
            "        fileSizeDiv.className = 'file-size';" +
            "        fileSizeDiv.textContent = fileSize;" +
            "        fileInfoDiv.appendChild(fileNameDiv);" +
            "        fileInfoDiv.appendChild(fileSizeDiv);" +
            "        let fileSuffixDiv = document.createElement('img');" +
            "        fileSuffixDiv.className = 'file-suffix';" +
            "        fileSuffixDiv.src = fileSuffix;" +
            "        fileDiv.appendChild(fileInfoDiv);" +
            "        fileDiv.appendChild(fileSuffixDiv);" +
            "        ownMessageDiv.appendChild(fileDiv);" +
            "        ownDiv.appendChild(ownMessageDiv);" +
            "        document.getElementById('chat').appendChild(ownDiv);" +
            "    }" +
            "</script>" +
            "</head>" +
            "<body>" +
            "<div id='chat' class='main'></div>" +
            "</body>" +
            "</html>";
    public final static String DIALOGUE_OWN_MESSAGE_JS = "ownMessage('%s','%s')";
    public final static String DIALOGUE_FRIEND_MESSAGE_JS = "friendMessage('%s','%s')";
    public final static String DIALOGUE_OWN_FILE_JS = "ownFileMessage('%s','%s','%s','%s')";
    public final static String DIALOGUE_FRIEND_FILE_JS = "friendFileMessage('%s','%s','%s','%s')";
    public final static String DIALOGUE_TIME_JS = "document.getElementById('chat').innerHTML += \"<div class='time'><div class='time-value'>%s</div></div>\"";
    public final static String DIALOGUE_SCROLL_JS = "window.scrollTo(0, document.body.scrollHeight);";
    public final static String FILE_EXCEL_ICO = "data:image/png;base64,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";
    public final static String FILE_DOC_ICO = "data:image/png;base64,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";
    public final static String FILE_UNKNOWN_ICO = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAAAXNSR0IArs4c6QAABYNJREFUeF7tm02MFEUUx/+vB3azHtAQjTHRg0HcGNT4RfwIMSoHOUiCkhGYqm4uRoMJihJxERBB5EsFRQJGL7vdVb1rNoLRwyqJH2gURKPGaEKWxHjwhCEkhogkTD/pdXazM9NMd89Usz0uc+1Xb+r/q/deve6uJiT82UJsIWAhiGYmHJLW7E8Ghhg4rJTam3Zws/aUZKAt5WEC7kxia8hmr6vUk4Z8NXQTC8CR8gUAr1yIyYz/D2be5Wn9dNb/Gw9AiB9BdEvWEzmP/8wjIR6AlFy1MsBBAr4wCGR9I18M7PSUetbg/1W5igNAjpRBTWhu9LRuOOkUk63zHzWWmd/wtH4mhd/Epm0BIFSTFYSJBgCnJsUA7GbmxUR0ee0yMvNWT+vViZc3gWEeASyHZR3gcvnrCwFhogHU1wDmp1yt33Ic5/rzQQCwwVXqpQQLHGuSWwDhzCsQviSiKyOU9LhKbYtVGGOQawDh3G3bvhZBcCgrCLkHkADCClepN5uNhLYAkCWEtgGQFYS2AhAHgZmXeVq/nSYd2g6AaQhtCcAkhDgAUa2qsSZkZK+vb4WXu0rtThLGSxctmhFMnfoVAVfV2gfAE0qpd+L85A8A8xCIjsRNfOw68wwQySh7Kpdv7Ovv/7WRrzwA+AHArYkFpzFk3u9q/UjeAWT2yI2Z//C0vibXAEbqgBDfg+j2NIub1NZVqmGUT3gKjApxpHyVgQUEXJdUXBK7VgFk/UgsiYbENrYQO4loxfgBkwuAlDsIqHp2eBFAizWgrVLAkfJ1AFWP0FuNAGOdoBBiboFIMDCbgKsBXDaSq8yfgmjg9JkzA4ODg6cSJ3yEoS3Ea0S00mQNaBnAuS3uISbaTsANMeJOVNrX95uFkDsAthCbiSjVY2xmXutp3dS7SEeI7SB6LhcR4Ej5GIB3m1pNy+p2XXc47VhbiG1EtMokgKaLYLFYnN7V2fk5gJvTCmn2sXeuIiAUXSwWL+3q6Ajv7u7+r97xemL+2PX9I0tLpXsDywprQ925AwY+85SamxacI+VWAM+bjICWi+Dj8+dfcnratH0W0Z4+pT4cP7mlS5bM4kLhlwihw65S3f8LAI1EFIvFrq7Ozr/rbJiPulrH7Rp1w3IZAY0ASCnnWcBQrQ0DH3hKPdxEBIS7R3h7PfbLTSNUK2Zk9Ts6vgPRrDoAQTDP8/1P0gKI2nZzC8CWcj8BC+rEM+/ztF6YVnxo3zYAbCn7CHAicv9YGbhDa/1XkwA2EdGaXKeAlHKZBeyJyPvj5SCY4/v+sWbEVyIg3wBKpdLMKZZV1+Ex86lCEMzu7e8/2qz4tgDgSOkDWBJR9B71fH+wFfEVABuI6MVcpkCDPf+Qq/U9rYoPxztSbgSwziSApu8FagVJKbstICrEV7pK7cgrgJZb4VFhQoibCkQ/R1T+xee6vvcMAQjPDVWdYWy1DzAWASYExvlwpLwIYLJHQBj+VcfnWk0BYzVgNHyllLcBeKAADNfeHseFeNx1R8pwBwh3grFfrgDYQqwiorGzfQx86yl1V5ywpNftUmktWdbLJgEYLYKOlMcBXDF+gkx0n+d5B5OKbGSXRQSYBSDECRBNHy+CmB/s0/qACQBZRIDRGmBLGb6rFqNimflkmXmG7/snjQAQYg0RbTKZAkYBVPr1lUR0PwPDhbNnd/UODPxuQnzF92oi2mwSgNEUMCX0fH6iPvBqdRe4CCDjb4aMBoUtxOROAVuIHiLaYrIG1BVBNv/ZnLkoYJ4Doqo3Sq3WgBBAduf4zEmP9sT8k6t1wzOIsafEosIq63mb8s9BsM7z/aq+oNZ3LIBwgCPEN6MvOE1NLnM/CVY/nEMiAJUmI+vP540wYebfiOgja8qUnt7e3n/inP4LXuDmbiXi8KYAAAAASUVORK5CYII=";
}

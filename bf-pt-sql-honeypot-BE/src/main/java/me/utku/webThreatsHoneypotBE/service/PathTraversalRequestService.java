package me.utku.webThreatsHoneypotBE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.webThreatsHoneypotBE.dto.Document;
import me.utku.webThreatsHoneypotBE.dto.Folder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class PathTraversalRequestService {

    public Folder generateFakeFolderStructure(){
        Document passwd = new Document("passwd",
            "root:x:0:0:root:/root:/bin/bash\n" +
                "daemon:x:1:1:daemon:/usr/sbin:/usr/sbin/nologin\n" +
                "bin:x:2:2:bin:/bin:/usr/sbin/nologin\n" +
                "sys:x:3:3:sys:/dev:/usr/sbin/nologin\n" +
                "sync:x:4:65534:sync:/bin:/bin/sync\n" +
                "games:x:5:60:games:/usr/games:/usr/sbin/nologin\n" +
                "man:x:6:12:man:/var/cache/man:/usr/sbin/nologin\n" +
                "lp:x:7:7:lp:/var/spool/lpd:/usr/sbin/nologin\n" +
                "mail:x:8:8:mail:/var/mail:/usr/sbin/nologin\n" +
                "news:x:9:9:news:/var/spool/news:/usr/sbin/nologin\n" +
                "uucp:x:10:10:uucp:/var/spool/uucp:/usr/sbin/nologin\n" +
                "proxy:x:13:13:proxy:/bin:/usr/sbin/nologin\n" +
                "www-data:x:33:33:www-data:/var/www:/usr/sbin/nologin\n" +
                "backup:x:34:34:backup:/var/backups:/usr/sbin/nologin\n" +
                "list:x:38:38:Mailing List Manager:/var/list:/usr/sbin/nologin\n" +
                "irc:x:39:39:ircd:/var/run/ircd:/usr/sbin/nologin\n" +
                "gnats:x:41:41:Gnats Bug-Reporting System (admin):/var/lib/gnats:/usr/sbin/nologin\n" +
                "nobody:x:65534:65534:nobody:/nonexistent:/usr/sbin/nologin\n" +
                "_apt:x:100:65534::/nonexistent:/usr/sbin/nologin\n" +
                "peter:x:12001:12001::/home/peter:/bin/bash\n" +
                "carlos:x:12002:12002::/home/carlos:/bin/bash\n" +
                "user:x:12000:12000::/home/user:/bin/bash\n" +
                "elmer:x:12099:12099::/home/elmer:/bin/bash\n" +
                "academy:x:10000:10000::/academy:/bin/bash\n" +
                "messagebus:x:101:101::/nonexistent:/usr/sbin/nologin\n" +
                "dnsmasq:x:102:65534:dnsmasq,,,:/var/lib/misc:/usr/sbin/nologin\n" +
                "systemd-timesync:x:103:103:systemd Time Synchronization,,,:/run/systemd:/usr/sbin/nologin\n" +
                "systemd-network:x:104:105:systemd Network Management,,,:/run/systemd:/usr/sbin/nologin\n" +
                "systemd-resolve:x:105:106:systemd Resolver,,,:/run/systemd:/usr/sbin/nologin\n" +
                "mysql:x:106:107:MySQL Server,,,:/nonexistent:/bin/false\n" +
                "postgres:x:107:110:PostgreSQL administrator,,,:/var/lib/postgresql:/bin/bash\n" +
                "usbmux:x:108:46:usbmux daemon,,,:/var/lib/usbmux:/usr/sbin/nologin\n" +
                "rtkit:x:109:115:RealtimeKit,,,:/proc:/usr/sbin/nologin\n" +
                "avahi:x:110:117:Avahi mDNS daemon,,,:/var/run/avahi-daemon:/usr/sbin/nologin\n" +
                "cups-pk-helper:x:111:118:user for cups-pk-helper service,,,:/home/cups-pk-helper:/usr/sbin/nologin\n" +
                "geoclue:x:112:119::/var/lib/geoclue:/usr/sbin/nologin\n" +
                "saned:x:113:121::/var/lib/saned:/usr/sbin/nologin\n" +
                "colord:x:114:122:colord colour management daemon,,,:/var/lib/colord:/usr/sbin/nologin\n" +
                "pulse:x:115:123:PulseAudio daemon,,,:/var/run/pulse:/usr/sbin/nologin\n" +
                "gdm:x:116:125:Gnome Display Manager:/var/lib/gdm3:/bin/false");
        Document shadow = new Document("shadow",null);
        Document fdprm = new Document("fdprm",null);
        Document fstab = new Document("fstab",null);
        Document group = new Document("group",null);
        Document inittab = new Document("inittab",null);
        Document issue = new Document("issue",null);
        Document magic = new Document("magic",null);
        Document motd = new Document("motd",null);
        Document mtab = new Document("mtab",null);
        Document logindefs = new Document("login.defs",null);
        Document printcap = new Document("printcap",null);
        Document securetty = new Document("securetty",null);
        Document shells = new Document("shells",null);
        Document termcap = new Document("termcap",null);

        Folder etc = new Folder(null,new Document[]{
            passwd, shadow, fdprm,
            fstab, group, inittab,
            issue, magic, motd,
            mtab, logindefs, printcap,
            securetty, shells, termcap
        }, "etc");
        Folder home = new Folder(null,null, "home");
        Folder var = new Folder(null,null, "var");

        Folder bin = new Folder(null,null, "bin");
        Folder usr = new Folder(null,null, "usr");
        Folder lib = new Folder(null,null, "lib");

        Folder tmp = new Folder(null,null, "tmp");
        Folder opt = new Folder(null,null, "opt");
        Folder dev = new Folder(null,null, "dev");

        Folder boot = new Folder(null,null, "boot");
        Folder srv = new Folder(null,null, "srv");
        Folder media = new Folder(null,null, "media");

        Folder data = new Folder(null,null, "data");
        Folder logs = new Folder(null,null, "logs");
        Folder backups = new Folder(null,null, "backups");

        Folder rootLinux = new Folder(new Folder[]{
            etc, home, var,
            bin, usr, lib,
            tmp, opt, dev,
            boot, srv, media,
            data, logs, backups
        },null,"/");
        return rootLinux;
    }
}

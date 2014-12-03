import socket

from sys import argv
from time import sleep
from multiprocessing import Process, Lock

try:
    localport = int(argv[1])
    username = argv[2]
except:
    try:
        localport = int(raw_input("Please enter a valid port number between 1025 and 65535: "))
        username = raw_input("Please enter a valid username: ")
    except:
        print "Invalid localport/username combination. Exiting..."
        exit()

if localport > 0xFFFF or localport < 1024:
    print "Bad port number. Please try again. Exiting..."
    exit()

server_timeout = 2

udpsock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
tcpsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

tcpsock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serversock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

udpsock.bind(('localhost', localport))
tcpsock.bind(('localhost', localport))
serversock.bind(('localhost', localport))

serversock.listen(1)

address = ('localhost', 12358)

auction = []
# example of adding an item 
    # auction.append([$description,$port,$current_bid,$client_bid])
loop = True
mutex = Lock()
auction_mutex = Lock()

def register():
    register = b'\x04\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:][-4:-2],16)) + chr(int(hex(localport)[2:][-2:],16))
    with mutex:
        udpsock.sendto(register, address)
        udpsock.settimeout(server_timeout)
        try:
            response = udpsock.recv(1024)
        except Exception as e:
            print e
            return 0,0
    if ord(response[0]) != 3:
        print 0,0
    with mutex:
        serversock.settimeout(10)
        return serversock.accept()

def deregister():
    register = b'\x02\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:4],16)) + chr(int(hex(localport)[4:6],16))
    udpsock.sendto(register, address)

    with mutex:
        try:
            udpsock.settimeout(server_timeout)
            response = udpsock.recv(1024)
            print repr(response)
            print "Deregistering success! Exiting..."
            loop = False
            exit()
        except Exception as e:
            print "Error while deregistering."
            print e

def offer(item,price):
    offer    = b'\x06\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01'+ chr(len(item)) +item+chr(int(hex(price)[2:][-4:-2].zfill(1),16))+chr(int(hex(price)[2:][-2:].zfill(1),16))
    with mutex:
        udpsock.sendto(offer, address)
        offerconf = udpsock.recv(1024)
        print 256*ord(offerconf[2]) + ord(offerconf[3])
    return True

def bid(item, price):
    for i in range(len(auction)):
        if auction[i][0] == item:
            tcpsock.connect(('localhost',auction[i][1]))
            with auction_mutex:
                tcpsock.send(b"\x00\0\0\0\0\0\0")
                try:
                    print repr(tcpsock.recv(100))
                except Exception as e:
                    print "Error in bidding"
                    print e
                    return False
            with mutex:
                auction[i][2] = price
                auction[i][3] = price
            return True

def remove(item):
    for i in range(len(auction)):
        if auction[i][0] == item:
            with auction_mutex:
                auction.pop(i)
            return True

try:
    # client is the server's response variable 'address'
    conn, client = register()
except Exception as e:
    print "Could not connect to server. Please try another port number."
    print e
    loop = False
    exit()

# If connection fails, sleep 2 seconds and try again. Eventually exit if 
# catastrophe happens
while (conn == 0) and (client == 0):
    try:
        # client is the server's response variable 'address'
        print "Could not connect to server. Attempting again..."
        conn, client = register_client()
    except Exception as e:
        print "Could not connect to server. Please try another port number."
        print e
        loop = False
        exit()

def listentoUDP():
    with mutex:
        try:
            udpsock.settimeout(server_timeout)
            resp = udpsock.recv(1024)
        except Exception as e:
            return False
def listentoSVR():
    with mutex:
        try:
            serversock.settimeout(server_timeout)
            resp = serversock.recv(1024)
        except Exception as e:
            return False

def listentoConn():
    with mutex:
        try:
            conn.settimeout(server_timeout)
            resp = conn.recv(1024)
        except Exception as e:
            return False
    #for i in range(len(resp)):
        #print repr(resp[i])
    if ord(resp[0]) == 8:
        print "New item!"
        port = 256*ord(resp[1]) + ord(resp[2])
        description = resp[4:4+ord(resp[3])]
        price = 256*ord(resp[-4])+ord(resp[-3])
        #print port
        #print description
        #print price
        with auction_mutex:
            auction.append([description,port,price,0])

def listen(n):
    for i in range(n):
        #listentoUDP()
        #listentoSVR()
        listentoConn()


p = Process(target = listen, args = (5,))
#p.start()

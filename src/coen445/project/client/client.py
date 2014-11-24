import socket

from sys import argv

try:
    localport = int(argv[1])
    username = argv[2]
except:
    try:
        localport = int(raw_input("Please enter a valid port number: "))
        username = raw_input("Please enter a valid username: ")
    except:
        print "Unspecified error. Exiting..."
        exit()

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
    # car = {"port" : 14533, "current_bid": 10, "client_bid": 10}
    # auction.append(car)

def register_client():
    register = b'\x04\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:4],16)) + chr(int(hex(localport)[4:6],16))
    udpsock.sendto(register, address)
    udpsock.recv(1024)
    serversock.settimeout(10)
    return serversock.accept()

def deregister_client():
    register = b'\x02\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:4],16)) + chr(int(hex(localport)[4:6],16))
    udpsock.sendto(register, address)
    udpsock.recv(1024)
    serversock.settimeout(10)
    try:
        result = serversock.accept()
        print result
    except:
        print "Server took too long to respond. Force quiting..."
    # TO-DO: CONFIRM THAT SERVER ACKNOWLEDGES DEREGISTER REQUEST
    exit()

def offer_item(item,price):
    offer    = b'\x06\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01\t'+item+chr(int(hex(price),16))+'\n'
    udpsock.sendto(offer, address)
    offerconf = udpsock.recv(1024)
    port = 256*ord(offerconf[2]) + ord(offerconf[3])
    return port

def bid_item(item):
    # 
    print "Place holder"

try:
    conn, client = register_client()
except:
    print "Could not connect to server. Please try another port number."
    exit()

port = offer_item("abcdefghijklmnopqrstuvwxyz",1)

#print ("receiving from server")
#print (repr(conn.recv(100)))

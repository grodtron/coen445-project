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

register = b'\x04\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01' + chr(int(hex(localport)[2:4],16)) + chr(int(hex(localport)[4:6],16))

def register_client(register, address):
    udpsock.sendto(register, address)
    udpsock.recv(1024)
    serversock.settimeout(10)
    return serversock.accept()

conn, client = register_client(register, address)

def offer_item(item):
    offer    = b'\x06\x00' + chr(len(username)) + username + b'\x7f\x00\x00\x01\t'+item+'\x00\n'
    udpsock.sendto(offer, address)
    offerconf = udpsock.recv(1024)
    return 256*ord(offerconf[2]) + ord(offerconf[3])

port = offer_item("abcdefghijklmnopqrstuvwxyz")

print ("receiving from server")
#print (repr(conn.recv(100)))

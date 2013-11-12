function server () {
	
	this.queue = new function () {
		this.fire = new function () {
			this.list = [];
			this.add = function (f) {
				this.list.push(f); // f = [name, x, y]
			};
			this.clear = function () { this.list = []; };
			this.find = function (n) {
				var i;
				for (i=0;i<this.list.length;i++) {
					if (this.list[i][0]==name) {
						return i;
					}
				}
			};
		};
		this.evade = new function () {
			this.list = [];
			this.add = function (v) {
				this.list.push(v); // v = [name, x, y]
			};
			this.clear = function () { this.list = []; };
			this.find = function (n) {
				var i;
				for (i=0;i<this.list.length;i++) {
					if (this.list[i][0]==n) {
						return this.list[i];
					}
				}
			};
		};
	};
	
	// loops through fire commands and compares to evade commands of targets
	this.tick = function () {
		var i;
		for (i=0;i<this.queue.fire.list.length;i++) {
			ev = this.queue.evade.find(this.queue.fire.list[i][0]);
			this.compare(this.queue.fire.list[i], ev);
		}
		this.queue.fire.clear();
		this.queue.evade.clear();
		this.clients.update();
	};
	
	// x and y coords from fire and evade compared
	// damage = maxdamage - (x+y)*damagedifference
	this.compare = function (fire, evade) {
		var x, y, d;
		x = ((fire[1] - evade[1])>0) ? fire[1] - evade[1] : evade[1] - fire[1];
		y = ((fire[2] - evade[2])>0) ? fire[2] - evade[2] : evade[2] - fire[2];
		d = server.fireDamageMax - (x+y)*server.fireDamageDiff;
		if (d>0) { this.clients.assignDamage(evade[0],d); }
	};
	
	this.clients = new function () {
		clients = [];
		sockets = [];
		
		this.add = function (name, socket) {
			function client () {
				var health = server.clientHealth;
				this.assignDamage = function (d) {
					health -= d;
				};
			}
			clients.push([name, server.clientHealth]);
			sockets.push([name,socket]);
		};
		this.remove = function (name) {
			var i;
			for (i=0;i<clients.length;i++) {
				if (clients[i][0]==name) {
					clients.splice(i,1);
					i = clients.length + 1;
				}
			}
		};
		this.assignDamage = function (name, damage) {
			var i;
			for (i=0;i<clients.length;i++) {
				if (clients[i][0]==name) {
					clients[i][1]-=damage;
					if (clients[i][1]<0) { clients[i][1] = 0; }
				}
			}
		};
		this.broadcast = function(message) {
			var i;
			for (i=0;i<sockets.length;i++) {
				// psuedo-code placeholder
				sockets[i][1].push (message);
			}
		};
		// send health updates to clients
		this.update = function () {
			var i;
			for (i=0;i<clients.length;i++) {
				this.broadcast(clients[i][0] + ":" + clients[i][1]);
			}
		};
	};
}
server.fireDamageMax = 5;
server.fireDamageDiff = 4;
server.maxCoordChange = 3;
server.clientHealth = 12;

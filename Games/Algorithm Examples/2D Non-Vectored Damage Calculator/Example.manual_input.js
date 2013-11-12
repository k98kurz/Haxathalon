
function client () {
	this.name = "default_client_name";
	this.outputElement = {};
	this.healthElement = {};
	this.health = 12;
	
	var parent = this;
	
	this.socket = new function () {
		this.push = function (d) {
			parent.outputElement.innerHTML += d + "<br>";
			if (d.indexOf(parent.name)>-1) {
				var p = parent.name.length;
				parent.health = parseInt(d.substring(p+1));
				parent.healthElement.innerHTML = parent.health;
			}
		}
	};
}

var Server = new server;

function tick () {
	var fire = {c1:[], c2:[]}, evade = {c1: [], c2:[]}, f, e, t, i, a,
		ids = [{id:"f1", type:"f"}, {id:"f2", type:"f"}, {id:"e1", type:"e"}, {id:"e2", type:"e"}];
	for (a=0;a<ids.length;a++) {
		t = $(ids[a].id).getElement();
		t = (ids[a].type=="f") ? t.firecoords : t.evadecoords;
		for (i=0;i<t.length;i++) {
			if (t[i].checked&&ids[a].type=="f") {
				if (ids[a].id.indexOf("1")<0) {
					fire.c2.push(client1.name)
					fire.c2.push(parseInt(t[i].value));
					fire.c2.push(parseInt(t[i].value.substring(2)));
				} else {
					fire.c1.push(client2.name)
					fire.c1.push(parseInt(t[i].value));
					fire.c1.push(parseInt(t[i].value.substring(2)));
				}
			} else if (t[i].checked&&ids[a].type=="e") {
				if (ids[a].id.indexOf("1")<0) {
					evade.c2.push(client2.name)
					evade.c2.push(parseInt(t[i].value));
					evade.c2.push(parseInt(t[i].value.substring(2)));
				} else {
					evade.c1.push(client1.name)
					evade.c1.push(parseInt(t[i].value));
					evade.c1.push(parseInt(t[i].value.substring(2)));
				}
			}
		}
	}
	Server.queue.fire.add(fire.c1);
	Server.queue.fire.add(fire.c2);
	Server.queue.evade.add(evade.c1);
	Server.queue.evade.add(evade.c2);
	Server.tick();
}

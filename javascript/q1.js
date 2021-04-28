function Person (name, age) {
     function D(one,two){
        this.name = one;
        this.age = two;
     }
    D.prototype.toString = ()=> { return {"Name":`${name}`,"Age":`${age}`} };
   return new D(name, age);
};


let p1= new Person ("Tester", 10); // create object
let p2= Person ("Tester", 10) ;// create object

console.log(p1.toString());
console.log(p2.toString());
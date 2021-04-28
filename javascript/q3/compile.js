
class Compile {
  constructor (el, vm) {
    this.el= typeof el === 'string' ? document.querySelector(el) : el
    this.vm= vm
    if (this.el) {
      let fragment = this.node2fragment(this.el)
      this.compile(fragment)
      this.el.appendChild(fragment)
    }
  }

  node2fragment (node) {
    let fragment = new DocumentFragment()
    let childNodes = node.childNodes
    this.toArray(childNodes).forEach(node => {
      fragment.appendChild(node)
    })
    return fragment
  }


  compile (fragment) {
    let childNodes = fragment.childNodes
    this.toArray(childNodes).forEach(node => {
      if (this.isElementNode(node)) {
        this.compileElement(node)
      }
      if (this.isTextNode(node)) {
        this.compileText(node)
      } 
      if (node.childNodes && node.childNodes.length > 0) {
        this.compile(node)
      }
    })
  }


  compileElement(node) {
    let attributes = node.attributes
    this.toArray(attributes).forEach(attr => {
      let attrName = attr.name
      if (this.isDirective(attrName)) {
        let type = attrName.slice(2)
        let expr = attr.value
        if (this.isEventDirective(type)) {
          CompileUtil["eventHanlder"](node, this.vm, type, expr)
        }
        else {
          CompileUtil[type] && CompileUtil[type](node, this.vm, expr)
        }
      }
    })
  }

  compileText(node) {
    CompileUtil.mustache(node, this.vm)
  }


  toArray (likeArray) {
    return [].slice.call(likeArray)
  }


  isElementNode (node) {
    return node.nodeType === 1
  }


  isTextNode (node) {
    return node.nodeType === 3
  }

  isDirective (attrName) {
    return attrName.startsWith('v-')
  }


  isEventDirective (type) {
    return type.split(':')[0] === 'on'
  }

}


let CompileUtil = {
  mustache (node, vm) {
    let text = node.textContent
    let reg = /\{\{(.+)\}\}/
    if (reg.test(text)) {
      let expr = RegExp.$1
      node.textContent = text.replace(reg, this.getVMValue(vm, expr))
      new Watcher(vm, expr, newValue => {
        node.textContent = newValue
      })
    }
  },
  text (node, vm, expr) {
    node.textContent = this.getVMValue(vm, expr)
    new Watcher(vm, expr, newValue => {
      node.textContent = newValue
    })
  },
  html (node, vm, expr) {
    node.innerHTML = this.getVMValue(vm, expr)
    new Watcher(vm, expr, newValue => {
      node.innerHTML = newValue
    })
  },
  model (node, vm, expr) {
    let seft = this
    node.value = this.getVMValue(vm, expr)
    node.addEventListener('input', function () {
      seft.setVMValue(vm, expr, this.value)
    })
    new Watcher(vm, expr, newValue => {
      node.value = newValue
    })
  },
  eventHanlder (node, vm, type, expr) {
    let eventType = type.split(":")[1]
    let fn = vm.$methods && vm.$methods[expr]
    if (eventType && fn) {
      node.addEventListener(eventType, fn.bind(vm))
    }
  },
  getVMValue (vm , expr) {
    let data = vm.$data
    expr.split(".").forEach(key => {
      data = data[key]
    })
    return data
  },
  setVMValue(vm , expr, value) {
    let data = vm.$data
    let arr = expr.split('.')
    arr.forEach((key, index) => {
      if (index < arr.length - 1) {
        data = data[key]
      }
      else {
        data[key] = value
      }
    })
  }
}
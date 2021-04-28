
class Observer {
  constructor (data) {
    this.data = data
    this.walk(this.data)
  }

  walk (data) {
    if (!data || typeof data !== 'object') {
      return
    }

    Object.keys(data).forEach(key => {
      this.defineReactive(data, key, data[key])
      this.walk(data[key])
    })
  }

  defineReactive (obj, key, value) {
    let self = this
    let dep = new Dep()
    Object.defineProperty(obj, key, {
      enumerable: true,
      configurable: true,
      get () {
        Dep.target && dep.addSub(Dep.target)
        return value
      },
      set (newValue) {
        if (value === newValue) {
          return
        }
        value = newValue
        self.walk(newValue)
        dep.notify()
      }
    })
  }
}
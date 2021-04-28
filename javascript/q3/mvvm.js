
class MVVM {
  constructor (options = {}) {
    this.$el = options.el
    this.$data = options.data
    this.$methods = options.methods

    new Observer(this.$data)

    this.proxy(this.$data)

    this.proxy(this.$methods)

    if (this.$el) {
      let c = new Compile(this.$el, this)
    }
  }

  proxy (data) {
    Object.keys(data).forEach(key => {
      Object.defineProperty(this, key, {
        enumerable: true,
        configurable: true,
        get () {
          return data[key]
        },
        set (newValue) {
          if (data[key] === newValue) {
            return
          }
          data[key] = newValue
        }
      })
    })
  }
}
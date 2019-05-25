// vars/ubuntu.groovy
def call(Closure body) {
    node('ubuntu-linux') {
        body()
    }
}
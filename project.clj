(defproject psswrd-solver "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-webdriver "0.7.2"]
                 [org.seleniumhq.selenium/selenium-java "2.52.0"]
                 [org.seleniumhq.selenium/selenium-htmlunit-driver "2.52.0"]]
  :main ^:skip-aot psswrd-solver.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
